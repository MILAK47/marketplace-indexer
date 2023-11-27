package com.onlydust.marketplace.indexer.domain.services.exposers;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanPullRequest;
import com.onlydust.marketplace.indexer.domain.models.exposition.Contribution;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubCodeReview;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubCommit;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubPullRequest;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.PullRequestIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.ContributionStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.PullRequestStorage;
import lombok.AllArgsConstructor;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.stream.Stream;

@AllArgsConstructor
public class PullRequestExposer implements PullRequestIndexer {
    PullRequestIndexer indexer;
    ContributionStorage contributionStorage;
    PullRequestStorage pullRequestStorage;

    @Override
    @Transactional
    public Optional<CleanPullRequest> indexPullRequest(String repoOwner, String repoName, Long pullRequestNumber) {
        final var pullRequest = indexer.indexPullRequest(repoOwner, repoName, pullRequestNumber);
        pullRequest.ifPresent(this::expose);

        return pullRequest;
    }

    private void expose(CleanPullRequest pullRequest) {
        final var fromPullRequest = Stream.of(pullRequest).map(GithubPullRequest::of).map(Contribution::of);
        final var fromCommits = pullRequest.getCommits().stream().map(commit -> GithubCommit.of(commit, pullRequest)).map(Contribution::of);
        final var fromCodeReviewsPending = pullRequest.getReviews().stream()
                .map(review -> GithubCodeReview.of(review, pullRequest))
                .filter(codeReview -> !codeReview.getState().isCompleted())
                .map(Contribution::of);
        final var fromCodeReviewsCompleted = pullRequest.getReviews().stream()
                .map(review -> GithubCodeReview.of(review, pullRequest))
                .filter(codeReview -> codeReview.getState().isCompleted())
                .map(Contribution::of);
        final var fromRequestedReviewers = pullRequest.getRequestedReviewers().stream()
                .map(reviewer -> GithubCodeReview.of(reviewer, pullRequest))
                .map(Contribution::of);

        final var contributions = Stream.of(fromPullRequest, fromCommits, fromCodeReviewsPending, fromCodeReviewsCompleted, fromRequestedReviewers)
                .flatMap(s -> s).toArray(Contribution[]::new);

        contributionStorage.deleteAllByRepoIdAndGithubNumber(pullRequest.getRepo().getId(), pullRequest.getNumber());
        contributionStorage.saveAll(contributions);
        pullRequestStorage.saveAll(GithubPullRequest.of(pullRequest));
    }
}
