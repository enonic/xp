package com.enonic.xp.storage.spi;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Typed records mirroring today's storage-index document shapes 1:1
 * (BranchIndexPath / VersionIndexPath / CommitIndexPath), so the mapping from the
 * existing *StorageRequestFactory layer is mechanical. Draft: field-level javadoc
 * and builders omitted; these become proper immutable value classes in Phase 0.
 */
public final class Records
{
    private Records() {}

    /** Tenant identity — resolved from authn at the NoDB boundary; implicit for embedded mode. */
    public record TenantRef(String tenantId) {}

    public record RepoRef(TenantRef tenant, String repoId) {}

    /** BRANCH document: head pointer per (branch, node). */
    public record BranchEntryRecord(
        String branch,
        String nodeId,
        String versionId,
        String nodePath,
        String nodeDataHash,
        String indexConfigHash,
        String aclHash,
        Instant timestamp ) {}

    /** VERSION document: immutable version metadata; payloads referenced by content hash. */
    public record VersionRecord(
        String versionId,
        String nodeId,
        String nodePath,
        Instant timestamp,
        String nodeDataHash,
        String indexConfigHash,
        String aclHash,
        List<String> binaryKeys,
        String commitId,
        Map<String, String> attributes ) {}

    /** COMMIT document. */
    public record CommitRecord(
        String commitId,
        String message,
        String committer,
        Instant timestamp ) {}

    public record Page(int from, int size) {}
}
