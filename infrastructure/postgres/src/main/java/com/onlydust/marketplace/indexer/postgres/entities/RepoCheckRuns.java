package com.onlydust.marketplace.indexer.postgres.entities;

import com.onlydust.marketplace.indexer.domain.models.raw.RawCheckRuns;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.ZonedDateTime;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(RepoCheckRuns.Id.class)
@Table(name = "repo_check_runs", schema = "indexer_raw")
public class RepoCheckRuns {
    @javax.persistence.Id
    @Column(name = "repo_id")
    Long repoId;
    @javax.persistence.Id
    @Column(name = "sha")
    String sha;
    @Column(name = "data")
    @Type(type = "jsonb")
    RawCheckRuns data;
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    ZonedDateTime createdAt;
    @Column(name = "updated_at")
    @UpdateTimestamp
    ZonedDateTime updatedAt;

    public static RepoCheckRuns of(Long repoId, String sha, RawCheckRuns checkRuns) {
        return RepoCheckRuns.builder().repoId(repoId).sha(sha).data(checkRuns).build();
    }

    @EqualsAndHashCode
    @AllArgsConstructor
    public static class Id implements Serializable {
        Long repoId;
        String sha;
    }
}
