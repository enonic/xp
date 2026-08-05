package com.enonic.xp.storage.nodb;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.protobuf.ByteString;

import com.enonic.nodb.proto.v1.BranchEntry;
import com.enonic.nodb.proto.v1.Commit;
import com.enonic.nodb.proto.v1.Version;

/**
 * Minimal in-memory stand-in for a NoDB tenant schema, backing {@link StubNodeStoreService}
 * / {@link StubRepositoryAdminService} in tests. Chosen over spinning up the real
 * nodb/engine + testcontainers Postgres (nodb is a separate Gradle build with no
 * cross-build artifact dependency in Phase 1 -- see this module's build.gradle header
 * comment) or duplicating a full server harness: fastest to write, no Docker requirement,
 * and sufficient to exercise the client's RPC mapping + status translation, which is what
 * Gate B's unit tests are actually about (nodb/BUILD-PHASE-1.md Gate B test-approach
 * pragmatism note).
 */
final class FakeNodbState
{
    final Set<String> repos = ConcurrentHashMap.newKeySet();

    /** key: repo|branch|nodeId */
    final Map<String, BranchEntry> branchEntriesById = new ConcurrentHashMap<>();

    /** key: repo|branch|nodePath -> nodeId, mirrors the engine's rebuildable path index. */
    final Map<String, String> nodeIdByPath = new ConcurrentHashMap<>();

    /** key: repo|versionId — version identity is (repo, version_id), mirroring node_version's PK (Phase 3.5 gate P2). */
    final Map<String, Version> versions = new ConcurrentHashMap<>();

    /** key: repo|commitId — commit addressing is repo-scoped on the wire (Phase 3.5 gate A). */
    final Map<String, Commit> commits = new ConcurrentHashMap<>();

    /** key: content hash ("sha256:&lt;hex&gt;") -- Phase 3 Gate B's node-payload segment pool. */
    final Map<String, ByteString> payloads = new ConcurrentHashMap<>();

    static String entryKey( final String repo, final String branch, final String nodeId )
    {
        return repo + "|" + branch + "|" + nodeId;
    }

    static String pathKey( final String repo, final String branch, final String path )
    {
        return repo + "|" + branch + "|" + path;
    }

    static String versionKey( final String repo, final String versionId )
    {
        return repo + "|" + versionId;
    }

    static String commitKey( final String repo, final String commitId )
    {
        return repo + "|" + commitId;
    }
}
