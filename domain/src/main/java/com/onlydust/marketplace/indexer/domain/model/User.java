package com.onlydust.marketplace.indexer.domain.model;


import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
public class User extends JsonDocument {
    Integer id;
    String login;
}
