package com.onlydust.marketplace.indexer.domain.services;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanIssue;
import com.onlydust.marketplace.indexer.domain.models.exposition.Contribution;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAccount;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubIssue;
import com.onlydust.marketplace.indexer.domain.ports.in.IssueIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.ContributionStorageRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class IssueContributionExposer implements IssueIndexer {
    IssueIndexer indexer;
    ContributionStorageRepository expositionRepository;

    @Override
    public CleanIssue indexIssue(String repoOwner, String repoName, Long issueNumber) {
        final var issue = indexer.indexIssue(repoOwner, repoName, issueNumber);

        final var fromAssignees = issue.getAssignees().stream().map(GithubAccount::of).map(assignee -> Contribution.of(GithubIssue.of(issue), assignee));

        final var contributions = fromAssignees.toArray(Contribution[]::new);

        expositionRepository.saveAll(contributions);

        return issue;
    }
}
