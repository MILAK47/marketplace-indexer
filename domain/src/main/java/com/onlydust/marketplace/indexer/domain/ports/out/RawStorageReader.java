package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.models.raw.*;

import java.util.List;
import java.util.Optional;

public interface RawStorageReader {
    Optional<RawRepo> repo(Long repoId);

    List<RawPullRequest> repoPullRequests(Long repoId);

    List<RawIssue> repoIssues(Long repoId);

    RawLanguages repoLanguages(Long repoId);

    Optional<RawUser> user(Long userId);

    List<RawSocialAccount> userSocialAccounts(Long userId);

    Optional<RawPullRequest> pullRequest(String repoOwner, String repoName, Long prNumber);

    Optional<RawIssue> issue(String repoOwner, String repoName, Long issueNumber);

    List<RawCodeReview> pullRequestReviews(Long pullRequestId);

    List<RawCommit> pullRequestCommits(Long pullRequestId);

    RawCheckRuns checkRuns(Long repoId, String sha);

    List<Long> pullRequestClosingIssues(String repoOwner, String repoName, Long pullRequestNumber);
}
