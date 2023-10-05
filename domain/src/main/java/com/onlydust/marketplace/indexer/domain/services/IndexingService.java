package com.onlydust.marketplace.indexer.domain.services;

import com.onlydust.marketplace.indexer.domain.exception.NotFound;
import com.onlydust.marketplace.indexer.domain.models.clean.*;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageReader;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class IndexingService {
    private final RawStorageReader rawStorageReader;

    public Repo indexRepo(Long repoId) {
        final var repo = rawStorageReader.repo(repoId).orElseThrow(() -> new NotFound("Repo not found"));
        final var pullRequests = rawStorageReader.repoPullRequests(repoId).stream().map(pr -> indexPullRequest(repo.getOwner().getLogin(), repo.getName(), pr.getNumber())).toList();
        final var issues = rawStorageReader.repoIssues(repoId).stream().map(issue -> indexIssue(repo.getOwner().getLogin(), repo.getName(), issue.getNumber())).toList();
        final var languages = rawStorageReader.repoLanguages(repoId);
        return new Repo(repo.getId(), pullRequests, issues, languages.get());
    }

    public User indexUser(Long userId) {
        final var user = rawStorageReader.user(userId).orElseThrow(() -> new NotFound("User not found"));
        final var socialAccounts = rawStorageReader.userSocialAccounts(userId);
        return new User(user.getId(), user.getLogin(), socialAccounts);
    }

    private List<CodeReview> indexPullRequestReviews(Long pullRequestId) {
        final var codeReviews = rawStorageReader.pullRequestReviews(pullRequestId);
        return codeReviews.stream().map(review -> {
            final var author = indexUser(review.getAuthor().getId());
            return new CodeReview(review.getId(), author);
        }).toList();
    }

    private List<Commit> indexPullRequestCommits(Long pullRequestId) {
        final var commits = rawStorageReader.pullRequestCommits(pullRequestId);
        return commits.stream().map(commit -> {
            final var author = Objects.isNull(commit.getAuthor()) ? commit.getCommitter() : commit.getAuthor();
            return Objects.isNull(author) ? null : new Commit(commit.getSha(), indexUser(author.getId()));
        }).filter(commit -> !Objects.isNull(commit)).toList();
    }

    private List<CheckRun> indexCheckRuns(Long repoId, String sha) {
        final var checkRuns = rawStorageReader.checkRuns(repoId, sha);
        return checkRuns.getCheckRuns().stream().map(checkRun -> {
            return new CheckRun(checkRun.getId());
        }).toList();
    }

    private List<Issue> indexClosingIssues(String repoOwner, String repoName, Long pullRequestNumber) {
        final var closingIssueNumbers = rawStorageReader.pullRequestClosingIssues(repoOwner, repoName, pullRequestNumber);
        return closingIssueNumbers.stream().map(issueNumber -> indexIssue(repoOwner, repoName, issueNumber)).toList();
    }

    public PullRequest indexPullRequest(String repoOwner, String repoName, Long prNumber) {
        final var pullRequest = rawStorageReader.pullRequest(repoOwner, repoName, prNumber).orElseThrow(() -> new NotFound("Pull request not found"));
        final var author = indexUser(pullRequest.getAuthor().getId());
        final var codeReviews = indexPullRequestReviews(pullRequest.getId());
        final var requestedReviewers = pullRequest.getRequestedReviewers().stream().map(reviewer -> indexUser(reviewer.getId())).toList();
        final var commits = indexPullRequestCommits(pullRequest.getId());
        final var checkRuns = indexCheckRuns(pullRequest.getHead().getRepo().getId(), pullRequest.getHead().getSha());
        final var closingIssues = indexClosingIssues(pullRequest.getBase().getRepo().getOwner().getLogin(), pullRequest.getBase().getRepo().getName(), pullRequest.getNumber());
        return new PullRequest(pullRequest.getId(), author, codeReviews, requestedReviewers, commits, checkRuns, closingIssues);
    }


    public Issue indexIssue(String repoOwner, String repoName, Long issueNumber) {
        final var issue = rawStorageReader.issue(repoOwner, repoName, issueNumber).orElseThrow(() -> new NotFound("Issue not found"));
        final var assignees = issue.getAssignees().stream().map(assignee -> indexUser(assignee.getId())).toList();
        return new Issue(issue.getId(), assignees);
    }
}
