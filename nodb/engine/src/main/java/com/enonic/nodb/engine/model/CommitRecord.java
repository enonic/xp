package com.enonic.nodb.engine.model;

import java.time.Instant;

/** COMMIT document equivalent — mirrors the {@code node_commit} table columns. */
public record CommitRecord(String commitId, String message, String committer, Instant timestamp)
{
}
