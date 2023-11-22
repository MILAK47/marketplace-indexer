package com.onlydust.marketplace.indexer.domain.models.raw;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
@ToString(callSuper = true)
public class RawIssue extends JsonDocument {
    Long id;
    Long number;
    String title;
    @JsonProperty("user")
    RawShortAccount author;
    String state;
    @JsonProperty("state_reason")
    String stateReason;
    @JsonProperty("html_url")
    String htmlUrl;
    String body;
    @JsonProperty("created_at")
    Instant createdAt;
    @JsonProperty("closed_at")
    Instant closedAt;
    @JsonProperty("updated_at")
    Instant updatedAt;
    @JsonProperty("pull_request")
    RawPullRequest pullRequest;
    Integer comments;
    List<RawShortAccount> assignees;
}
