package com.onlydust.marketplace.indexer.postgres.entities.raw;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.Instant;


@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@IdClass(PullRequestClosingIssue.Id.class)
@Table(name = "pull_request_closing_issues", schema = "indexer_raw")
public class PullRequestClosingIssue {
    @javax.persistence.Id
    @Column(name = "pull_request_id")
    Long pullRequestId;

    @javax.persistence.Id
    @Column(name = "issue_id")
    Long issueId;

    @CreationTimestamp
    @EqualsAndHashCode.Exclude
    @Column(name = "created_at", nullable = false, updatable = false)
    Instant createdAt;

    @UpdateTimestamp
    @EqualsAndHashCode.Exclude
    @Column(name = "updated_at", nullable = false)
    Instant updatedAt;

    public static PullRequestClosingIssue of(Long pullRequestId, Long issueId) {
        return PullRequestClosingIssue.builder().pullRequestId(pullRequestId).issueId(issueId).build();
    }

    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Id implements Serializable {
        Long pullRequestId;
        Long issueId;
    }
}

