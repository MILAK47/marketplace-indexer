package com.onlydust.marketplace.indexer.postgres.repositories;

import com.onlydust.marketplace.indexer.postgres.entities.RepoIndexingJobTriggerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface RepoIndexingJobTriggerEntityRepository extends JpaRepository<RepoIndexingJobTriggerEntity, Long> {
    void deleteAllByInstallationId(Long installationId);

    @Query("SELECT DISTINCT installationId FROM RepoIndexingJobTriggerEntity")
    Set<Long> listDistinctInstallationIds();

    @Query("SELECT DISTINCT repoId FROM RepoIndexingJobTriggerEntity WHERE (:installationId IS NULL AND installationId IS NULL) OR (:installationId IS NOT NULL AND installationId = :installationId)")
    Set<Long> findDistinctRepoIdsByInstallationId(Long installationId);
}
