package com.onlydust.marketplace.indexer.domain.jobs;

import com.onlydust.marketplace.indexer.domain.ports.in.contexts.GithubAppContext;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.FullRepoIndexer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@AllArgsConstructor
@Slf4j
public class RepoIndexerJob extends Job {
    final FullRepoIndexer fullRepoIndexer;
    final Long installationId;
    final Set<Long> repoIds;
    GithubAppContext githubAppContext;

    @Override
    public void execute() {
        LOGGER.info("Indexing installation {} for repos {}", installationId, repoIds);
        githubAppContext.withGithubApp(installationId,
                () -> repoIds.forEach(repo -> {
                    if (fullRepoIndexer.indexFullRepo(repo).isEmpty())
                        LOGGER.warn("Repo {} not found", repo);
                })
        );
    }

    @Override
    public String name() {
        return String.format("repo-indexer-%d", installationId);
    }
}
