package com.enonic.nodb.engine.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * VERSION document equivalent (immutable, append-only) — mirrors the {@code node_version}
 * table columns in schema.sql v0.3 1:1. Payloads (node data / index config / ACL) are
 * referenced by content hash ({@code "sha256:<hex>"}), not embedded.
 */
public record VersionRecord(String versionId, String nodeId, String nodePath, Instant timestamp, String nodeDataHash,
                             String indexConfigHash, String aclHash, List<String> binaryKeys, String commitId,
                             Map<String, String> attributes)
{
}
