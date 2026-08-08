package com.enonic.nodb.engine.snapshot;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.zip.GZIPInputStream;
import javax.sql.DataSource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.enonic.nodb.engine.TenantContext;
import com.enonic.nodb.engine.TenantProvisioner;
import com.enonic.nodb.engine.Tx;
import com.enonic.nodb.engine.model.BranchEntryRecord;
import com.enonic.nodb.engine.model.CommitRecord;
import com.enonic.nodb.engine.model.RepoRef;
import com.enonic.nodb.engine.model.VersionRecord;
import com.enonic.nodb.engine.search.OutboxStore;
import com.enonic.nodb.engine.search.SearchDocument;
import com.enonic.nodb.engine.search.SearchDocumentStore;
import com.enonic.nodb.engine.store.PayloadRef;
import com.enonic.nodb.engine.store.RepoKeys;
import com.enonic.nodb.engine.store.RepositoryLifecycle;
import com.enonic.nodb.engine.store.WriteBatchRequest;
import com.enonic.nodb.engine.store.WriteService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Phase 5 Gate A's verification row, as amended by ratified decision #2 (FULL sorted hash
 * manifest, not deltas): snapshot under concurrent writers is consistent (no torn repo, no
 * torn outbox seq); the manifest is exactly the distinct hash set the snapshot's rows
 * reference; dual-tenant isolation; horizon math at the boundaries (expiry stamped at
 * CREATION, never retroactively moved); crash-consistency of the CREATING→COMPLETE state
 * machine and of delete; per-repo and per-tenant registry round trips; and the
 * {@code search_document} COPY volume measurement (Gate 0 unknown #4).
 *
 * <p>Real Postgres + real MinIO (the {@code BinaryStoreTest} harness); the concurrency test
 * provokes its window with the proxied-DataSource machinery IndexerTest's P1/Gate-C
 * regressions established — keyed on a query, never on a connection ordinal.
 */
@Testcontainers
class SnapshotServiceTest
{
    private static final String BUCKET = "nodb-snapshot-test";

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>( "postgres:17" );

    @Container
    private static final MinIOContainer MINIO = new MinIOContainer( "minio/minio:RELEASE.2024-11-07T00-52-20Z" );

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final AtomicInteger REPO_SEQUENCE = new AtomicInteger();

    private static HikariDataSource dataSource;

    private static TenantProvisioner provisioner;

    private static S3Client s3;

    private static SnapshotObjectStore objects;

    private static SnapshotService service;

    @BeforeAll
    static void setUp()
    {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl( POSTGRES.getJdbcUrl() );
        config.setUsername( POSTGRES.getUsername() );
        config.setPassword( POSTGRES.getPassword() );
        config.setMaximumPoolSize( 8 );
        dataSource = new HikariDataSource( config );
        provisioner = new TenantProvisioner( dataSource, POSTGRES.getUsername() );

        s3 = S3Client.builder()
            .region( Region.US_EAST_1 )
            .endpointOverride( URI.create( MINIO.getS3URL() ) )
            .credentialsProvider(
                StaticCredentialsProvider.create( AwsBasicCredentials.create( MINIO.getUserName(), MINIO.getPassword() ) ) )
            .serviceConfiguration( S3Configuration.builder().pathStyleAccessEnabled( true ).build() )
            .build();
        s3.createBucket( CreateBucketRequest.builder().bucket( BUCKET ).build() );

        objects = new SnapshotObjectStore( s3, BUCKET );
        service = new SnapshotService( dataSource, objects );
    }

    @AfterAll
    static void tearDown()
    {
        dataSource.close();
        s3.close();
    }

    // ------------------------------------------------------------- registry metadata round trips

    @Test
    void perRepoSnapshotRoundTripsItsRegistryMetadata()
        throws Exception
    {
        TenantContext tenant = tenant( "trt" );
        String repoId = createRepo( tenant, "roundtrip" );

        seedNode( tenant, repoId, "master", "n1", "rt-one", List.of( sha( "bin-a" ), sha( "bin-b" ) ), "c1" );
        seedNode( tenant, repoId, "master", "n2", "rt-two", List.of( sha( "bin-b" ) ), null );
        seedNode( tenant, repoId, "master", "n3", "rt-three", List.of(), null );
        for ( String nodeId : List.of( "n1", "n2", "n3" ) )
        {
            shipDocument( tenant, repoId, "master", nodeId, "title " + nodeId );
        }
        long preSeq = maxSeq( tenant );

        SnapshotRecord snapshot = service.createRepoSnapshot( tenant, repoId );

        assertEquals( SnapshotRegistry.STATE_COMPLETE, snapshot.state() );
        assertEquals( SnapshotService.SCOPE_REPO, snapshot.scope() );
        assertEquals( repoId, snapshot.repoId() );
        assertNotNull( snapshot.repoKey() );
        assertEquals( preSeq, snapshot.outboxSeq(), "the outbox seq must be the snapshot transaction's own first read" );
        assertEquals( 3, snapshot.versionCount() );
        assertEquals( 3, snapshot.headCount() );
        assertEquals( 1, snapshot.commitCount() );
        assertEquals( 3, snapshot.documentCount() );
        // 3 nodes x 3 distinct payload segments + 2 distinct binary keys (bin-b deduped).
        assertEquals( 9 + 2, snapshot.hashCount() );
        assertTrue( snapshot.totalBytes() > 0 );
        assertEquals( SnapshotService.FORMAT_VERSION, snapshot.formatVersion() );

        // The registry row is what list() serves.
        List<SnapshotRecord> listed = service.list( tenant );
        assertEquals( 1, listed.size() );
        assertEquals( snapshot, listed.get( 0 ) );

        // The artifact set is complete, and MANIFEST.json's recorded sha256 per artifact matches
        // the stored bytes — the integrity chain restore will walk (registry.manifest_sha256 ->
        // MANIFEST.json -> every artifact).
        String prefix = snapshot.location();
        byte[] manifestBytes = objects.get( prefix + "MANIFEST.json" ).readAllBytes();
        assertEquals( "sha256:" + sha256Hex( manifestBytes ), snapshot.manifestSha256() );

        JsonNode manifest = MAPPER.readTree( manifestBytes );
        assertEquals( preSeq, manifest.path( "outboxSeq" ).asLong() );
        assertEquals( 1, manifest.path( "repos" ).size() );
        assertEquals( repoId, manifest.path( "repos" ).path( 0 ).path( "repoId" ).asText() );

        long summedBytes = 0;
        long summedRows = 0;
        for ( JsonNode artifact : manifest.path( "artifacts" ) )
        {
            byte[] stored = objects.get( prefix + artifact.path( "key" ).asText() ).readAllBytes();
            assertEquals( "sha256:" + sha256Hex( stored ), artifact.path( "sha256" ).asText(),
                          "artifact " + artifact.path( "key" ).asText() + " must hash to its manifest entry" );
            assertEquals( stored.length, artifact.path( "bytes" ).asLong() );
            summedBytes += stored.length;
            summedRows += artifact.path( "rows" ).asLong();
        }
        assertEquals( snapshot.totalBytes(), summedBytes + manifestBytes.length );
        // branches(2: master+draft) + versions(3) + heads(3) + commits(1) + documents(3) + hashes(11)
        assertEquals( 2 + 3 + 3 + 1 + 3 + 11, summedRows );
        assertEquals( 7, manifest.path( "artifacts" ).size(), "5 per-repo streams + 2 hash manifests" );
        assertEquals( 8, objects.listKeys( prefix ).size(), "the artifacts plus MANIFEST.json, nothing else" );
    }

    @Test
    void perTenantSnapshotCapturesEveryRepoAtOneConsistentPoint()
        throws Exception
    {
        TenantContext tenant = tenant( "tsnap" );
        String repoA = createRepo( tenant, "multi.a" );
        String repoB = createRepo( tenant, "multi.b" );

        seedNode( tenant, repoA, "master", "a1", "ts-a1", List.of(), "ca" );
        seedNode( tenant, repoA, "master", "a2", "ts-a2", List.of(), null );
        seedNode( tenant, repoB, "master", "b1", "ts-b1", List.of( sha( "ts-bin" ) ), null );
        shipDocument( tenant, repoA, "master", "a1", "alpha one" );
        shipDocument( tenant, repoB, "master", "b1", "bravo one" );
        long preSeq = maxSeq( tenant );

        SnapshotRecord snapshot = service.createTenantSnapshot( tenant );

        assertEquals( SnapshotService.SCOPE_TENANT, snapshot.scope() );
        assertNull( snapshot.repoId() );
        assertNull( snapshot.repoKey() );
        assertEquals( preSeq, snapshot.outboxSeq() );
        assertEquals( 3, snapshot.versionCount() );
        assertEquals( 3, snapshot.headCount() );
        assertEquals( 1, snapshot.commitCount() );
        assertEquals( 2, snapshot.documentCount() );
        // 3 nodes x 3 distinct segments + 1 binary key.
        assertEquals( 9 + 1, snapshot.hashCount() );

        JsonNode manifest = readManifest( snapshot );
        assertEquals( 2, manifest.path( "repos" ).size(), "every repo of the tenant, in repo_key order" );
        assertEquals( repoA, manifest.path( "repos" ).path( 0 ).path( "repoId" ).asText() );
        assertEquals( repoB, manifest.path( "repos" ).path( 1 ).path( "repoId" ).asText() );
        assertEquals( 2, manifest.path( "repos" ).path( 0 ).path( "versionCount" ).asLong() );
        assertEquals( 1, manifest.path( "repos" ).path( 1 ).path( "versionCount" ).asLong() );
        assertEquals( 1, manifest.path( "repos" ).path( 1 ).path( "documentCount" ).asLong() );
    }

    // ------------------------------------------------------------------------ concurrent writers

    /**
     * The gate's headline: writes committing MID-CREATE are invisible — the snapshot's row
     * counts and outbox seq are exactly the pre-commit state, across every repo (no torn
     * repo: repo A captured before the commit, repo B after, would disagree; here both are
     * read under the one repeatable-read snapshot {@code Tx.inTenantSnapshot} pins with its
     * first statement, the outbox-seq read).
     *
     * <p>The window is provoked with the proxied-DataSource machinery (IndexerTest's
     * pattern): the hook fires when the create prepares its repo-enumeration query — i.e.
     * strictly AFTER the outbox seq pinned the snapshot, strictly BEFORE any row stream —
     * and commits new versions, documents and a whole new repo through the clean pool.
     */
    @Test
    void snapshotUnderConcurrentWritersCapturesExactlyThePreCommitState()
        throws Exception
    {
        TenantContext tenant = tenant( "tconc" );
        String repoA = createRepo( tenant, "conc.a" );
        String repoB = createRepo( tenant, "conc.b" );

        for ( String nodeId : List.of( "a1", "a2" ) )
        {
            seedNode( tenant, repoA, "master", nodeId, "conc-" + nodeId, List.of(), null );
            shipDocument( tenant, repoA, "master", nodeId, "conc " + nodeId );
        }
        for ( String nodeId : List.of( "b1", "b2" ) )
        {
            seedNode( tenant, repoB, "master", nodeId, "conc-" + nodeId, List.of(), null );
            shipDocument( tenant, repoB, "master", nodeId, "conc " + nodeId );
        }

        long preSeq = maxSeq( tenant );
        long preVersions = 4;
        long preHeads = 4;
        long preDocuments = 4;

        AtomicBoolean hookRan = new AtomicBoolean();
        DataSource hooked = hookedBeforeQuery( dataSource,
                                               sql -> sql.contains( "FROM repository" ) && sql.contains( "ORDER BY repo_key" ), () -> {
                try
                {
                    // Committed writes into BOTH repos, plus a brand-new repo — all of it must be
                    // invisible to the create in flight.
                    seedNode( tenant, repoA, "master", "a-late", "conc-late-a", List.of( sha( "late-bin" ) ), null );
                    shipDocument( tenant, repoA, "master", "a-late", "late alpha" );
                    seedNode( tenant, repoB, "master", "b-late", "conc-late-b", List.of(), null );
                    shipDocument( tenant, repoB, "master", "b-late", "late bravo" );
                    String repoC = createRepo( tenant, "conc.c" );
                    seedNode( tenant, repoC, "master", "c1", "conc-c1", List.of(), null );
                    hookRan.set( true );
                }
                catch ( SQLException e )
                {
                    throw new IllegalStateException( "the concurrent commit is the provocation; it must succeed", e );
                }
            } );

        SnapshotService racingService = new SnapshotService( hooked, objects );
        SnapshotRecord snapshot = racingService.createTenantSnapshot( tenant );

        assertTrue( hookRan.get(), "the mid-create commit must have run; otherwise this test proves nothing" );
        assertTrue( maxSeq( tenant ) > preSeq, "the late writes really committed" );

        assertEquals( preSeq, snapshot.outboxSeq(), "the recorded seq must be the pre-commit state, not the late writes'" );
        assertEquals( preVersions, snapshot.versionCount(), "late versions must be invisible to the snapshot" );
        assertEquals( preHeads, snapshot.headCount() );
        assertEquals( preDocuments, snapshot.documentCount() );

        JsonNode manifest = readManifest( snapshot );
        assertEquals( 2, manifest.path( "repos" ).size(), "the concurrently created repo must not appear" );
        List<String> payloadHashes = gunzipLines( snapshot.location() + "hashes/payloads.txt.gz" );
        assertFalse( payloadHashes.contains( sha( "data-conc-late-a" ) ), "a late payload hash must not leak into the manifest" );
        List<String> binaryKeys = gunzipLines( snapshot.location() + "hashes/binaries.txt.gz" );
        assertFalse( binaryKeys.contains( sha( "late-bin" ) ), "a late binary key must not leak into the manifest" );
    }

    // -------------------------------------------------------------------------- the hash manifest

    /**
     * Ratified #2: the manifest is the FULL sorted list of exactly the DISTINCT hashes the
     * snapshot's rows reference. Seeded with dedup across repos (same payload bytes in two
     * repos — the tenant-shared pool stores them once), across branches (draft and master
     * heads over versions with identical content) and across versions (the same binary key
     * on rows in both repos) to prove DISTINCT semantics, not just collection.
     */
    @Test
    void theManifestIsExactlyTheDistinctHashSetTheSnapshotRowsReference()
        throws Exception
    {
        TenantContext tenant = tenant( "tdedup" );
        String repoA = createRepo( tenant, "dedup.a" );
        String repoB = createRepo( tenant, "dedup.b" );

        // "shared" content in both repos and on both branches of repo A; a shared binary key
        // in both repos; one unique content + binary per repo.
        seedNode( tenant, repoA, "master", "s", "shared", List.of( sha( "bin-shared" ) ), null );
        seedNode( tenant, repoA, "draft", "s", "shared", List.of( sha( "bin-shared" ) ), null );
        seedNode( tenant, repoB, "master", "s", "shared", List.of( sha( "bin-shared" ), sha( "bin-b-only" ) ), null );
        seedNode( tenant, repoA, "master", "ua", "unique-a", List.of( sha( "bin-a-only" ) ), null );
        seedNode( tenant, repoB, "master", "ub", "unique-b", List.of(), null );

        SnapshotRecord snapshot = service.createTenantSnapshot( tenant );

        TreeSet<String> expectedPayloads = new TreeSet<>();
        for ( String content : List.of( "shared", "unique-a", "unique-b" ) )
        {
            expectedPayloads.add( sha( "data-" + content ) );
            expectedPayloads.add( sha( "icfg-" + content ) );
            expectedPayloads.add( sha( "acl-" + content ) );
        }
        TreeSet<String> expectedBinaries = new TreeSet<>( List.of( sha( "bin-shared" ), sha( "bin-a-only" ), sha( "bin-b-only" ) ) );

        List<String> payloadLines = gunzipLines( snapshot.location() + "hashes/payloads.txt.gz" );
        List<String> binaryLines = gunzipLines( snapshot.location() + "hashes/binaries.txt.gz" );

        assertEquals( List.copyOf( expectedPayloads ), payloadLines,
                      "exactly the distinct payload hashes, sorted — dedup across repos, branches and versions" );
        assertEquals( List.copyOf( expectedBinaries ), binaryLines, "exactly the distinct binary keys, sorted" );
        assertEquals( expectedPayloads.size() + expectedBinaries.size(), snapshot.hashCount() );
    }

    // ------------------------------------------------------------------------- tenant isolation

    @Test
    void aTenantsSnapshotNeverSeesOrListsAnotherTenantsDataOrObjects()
        throws Exception
    {
        TenantContext tenantA = tenant( "tisoa" );
        TenantContext tenantB = tenant( "tisob" );
        String repoA = createRepo( tenantA, "iso" );
        String repoB = createRepo( tenantB, "iso" );
        seedNode( tenantA, repoA, "master", "n1", "iso-a-secret", List.of( sha( "iso-a-bin" ) ), null );
        seedNode( tenantB, repoB, "master", "n1", "iso-b-secret", List.of( sha( "iso-b-bin" ) ), null );

        SnapshotRecord snapshotA = service.createTenantSnapshot( tenantA );

        // Registry isolation: tenant B lists nothing, tenant A lists its own.
        assertEquals( List.of(), service.list( tenantB ) );
        assertEquals( List.of( snapshotA ), service.list( tenantA ) );

        // Data isolation: tenant B's hashes are nowhere in A's manifest.
        List<String> payloadHashes = gunzipLines( snapshotA.location() + "hashes/payloads.txt.gz" );
        assertTrue( payloadHashes.contains( sha( "data-iso-a-secret" ) ) );
        assertFalse( payloadHashes.contains( sha( "data-iso-b-secret" ) ) );
        assertFalse( gunzipLines( snapshotA.location() + "hashes/binaries.txt.gz" ).contains( sha( "iso-b-bin" ) ) );

        // Object isolation: every object of A's snapshot lives under A's tenant prefix, and
        // B's snapshot area is untouched.
        assertTrue( snapshotA.location().startsWith( "tisoa/snapshot/" ) );
        assertFalse( objects.listKeys( "tisoa/snapshot/" ).isEmpty() );
        assertEquals( List.of(), objects.listKeys( "tisob/snapshot/" ) );

        // And the reverse direction, for symmetry.
        SnapshotRecord snapshotB = service.createTenantSnapshot( tenantB );
        assertFalse( gunzipLines( snapshotB.location() + "hashes/payloads.txt.gz" ).contains( sha( "data-iso-a-secret" ) ) );
        assertEquals( List.of( snapshotA ), service.list( tenantA ), "B's snapshot must not appear in A's registry" );
    }

    // ----------------------------------------------------------------------------- horizon math

    /**
     * Decision 2 at the boundaries: {@code expires_at} is stamped from the policy AT
     * CREATION TIME (exact interval arithmetic, asserted in SQL against the row's own
     * {@code created_at}); a later policy change moves NOTHING retroactively; and a zero
     * horizon stamps {@code expires_at = created_at} exactly — the degenerate boundary.
     */
    @Test
    void expiryIsStampedFromThePolicyAtCreationAndNeverRetroactivelyMoved()
        throws Exception
    {
        TenantContext tenant = tenant( "texp" );
        String repoId = createRepo( tenant, "exp" );
        seedNode( tenant, repoId, "master", "n1", "exp-one", List.of(), null );

        setSnapshotHorizon( tenant, "7 days" );
        SnapshotRecord first = service.createRepoSnapshot( tenant, repoId );
        assertTrue( expiryEquals( tenant, first.snapshotId(), "7 days" ), "expires_at must be created_at + the policy at creation" );

        setSnapshotHorizon( tenant, "1 hour" );

        // The policy change must not move the existing snapshot's expiry — in either direction.
        SnapshotRecord firstAfterChange = service.list( tenant ).stream()
            .filter( record -> record.snapshotId().equals( first.snapshotId() ) )
            .findFirst()
            .orElseThrow();
        assertEquals( first.expiresAt(), firstAfterChange.expiresAt(), "a later policy change is not retroactive" );
        assertTrue( expiryEquals( tenant, first.snapshotId(), "7 days" ) );

        SnapshotRecord second = service.createRepoSnapshot( tenant, repoId );
        assertTrue( expiryEquals( tenant, second.snapshotId(), "1 hour" ), "a new snapshot stamps the NEW policy" );

        // Boundary: a zero horizon stamps expires_at = created_at exactly.
        setSnapshotHorizon( tenant, "0 seconds" );
        SnapshotRecord third = service.createRepoSnapshot( tenant, repoId );
        assertEquals( third.createdAt(), third.expiresAt(), "zero horizon: expires_at = created_at, the exact boundary" );
    }

    // ------------------------------------------------------------------------ crash consistency

    /**
     * The state machine under failure: the registry row exists (CREATING) BEFORE any object
     * is uploaded, so a create that dies mid-stream leaves an identifiable non-COMPLETE row
     * plus an orphan prefix — never a prefix that could be mistaken for a snapshot, never a
     * COMPLETE row over missing objects. An in-process failure is marked FAILED; a hard
     * crash (simulated below by resetting the row, since a unit test cannot kill the JVM
     * between two statements) leaves CREATING. Either way: identifiable, deletable, never
     * half-trusted — a re-create succeeds alongside, and delete removes both halves.
     */
    @Test
    void aCreateThatDiesBeforeCompleteIsIdentifiableDeletableAndNeverTrusted()
        throws Exception
    {
        TenantContext tenant = tenant( "tcrash" );
        String repoId = createRepo( tenant, "crash" );
        seedNode( tenant, repoId, "master", "n1", "crash-one", List.of(), null );
        shipDocument( tenant, repoId, "master", "n1", "doomed create" );

        // An S3 that dies on the third object PUT — mid-create, after the registry row and
        // after some artifacts are already durable.
        SnapshotService dyingService =
            new SnapshotService( dataSource, new SnapshotObjectStore( failingOnNthPut( s3, 3 ), BUCKET ) );

        RuntimeException failure =
            assertThrows( RuntimeException.class, () -> dyingService.createRepoSnapshot( tenant, repoId ) );
        assertTrue( failure.getMessage().contains( "injected S3 outage" ), "the injected failure must be the one that surfaced" );

        List<SnapshotRecord> rows = service.list( tenant );
        assertEquals( 1, rows.size() );
        SnapshotRecord dead = rows.get( 0 );
        assertEquals( SnapshotRegistry.STATE_FAILED, dead.state(), "an in-process failure is marked FAILED — never COMPLETE" );
        assertNull( dead.versionCount(), "counts are stamped only by the COMPLETE transition" );
        assertNull( dead.manifestSha256() );
        assertFalse( objects.listKeys( dead.location() ).isEmpty(), "the orphan prefix from the partial upload exists" );
        assertTrue( objects.listKeys( dead.location() ).stream().noneMatch( key -> key.endsWith( "MANIFEST.json" ) ),
                    "no manifest was written — the prefix cannot be mistaken for a snapshot" );

        // A HARD crash (process death before the best-effort FAILED mark) leaves the row in
        // CREATING. A test cannot kill the JVM between two statements, so that post-crash
        // state is reconstructed literally: same row, state as the crash would have left it.
        Tx.inTenantTx( dataSource, tenant, connection -> {
            try (PreparedStatement statement =
                     connection.prepareStatement( "UPDATE snapshot SET state = 'CREATING' WHERE snapshot_id = ?" ))
            {
                statement.setString( 1, dead.snapshotId() );
                statement.executeUpdate();
            }
            return null;
        } );
        assertEquals( SnapshotRegistry.STATE_CREATING, service.list( tenant ).get( 0 ).state(),
                      "the crashed create is identifiable as CREATING in the registry" );

        // A re-create succeeds ALONGSIDE the crashed one — the corpse blocks nothing.
        SnapshotRecord recreated = service.createRepoSnapshot( tenant, repoId );
        assertEquals( SnapshotRegistry.STATE_COMPLETE, recreated.state() );
        assertEquals( 2, service.list( tenant ).size(), "the crashed row and the healthy snapshot coexist" );

        // Delete removes both halves of the corpse: registry row AND orphan prefix.
        assertTrue( service.delete( tenant, dead.snapshotId() ) );
        assertEquals( List.of( recreated ), service.list( tenant ) );
        assertEquals( List.of(), objects.listKeys( dead.location() ), "the orphan prefix is swept" );
        assertFalse( objects.listKeys( recreated.location() ).isEmpty(), "the healthy snapshot's objects are untouched" );
    }

    // --------------------------------------------------------------------------------- delete

    /**
     * Delete = registry row FIRST, S3 prefix SECOND (the crash argument is in
     * {@link SnapshotService}'s Javadoc: an orphan prefix is identifiable garbage; the
     * reverse — a COMPLETE row over a half-deleted prefix — would be trusted and is not).
     * Delete refuses nothing: an EXPIRED snapshot deletes like any other (expiry is
     * restore's concern, Gate B), an unknown id is an idempotent no-op whose prefix sweep
     * still runs.
     */
    @Test
    void deleteRemovesRowThenPrefixAndRefusesNothing()
        throws Exception
    {
        TenantContext tenant = tenant( "tdel" );
        String repoId = createRepo( tenant, "del" );
        seedNode( tenant, repoId, "master", "n1", "del-one", List.of(), null );

        setSnapshotHorizon( tenant, "0 seconds" );
        SnapshotRecord expired = service.createRepoSnapshot( tenant, repoId );
        assertFalse( expired.expiresAt().isAfter( Instant.now() ), "the snapshot is already past its horizon" );

        assertTrue( service.delete( tenant, expired.snapshotId() ), "operator delete is always allowed — expired or not" );
        assertEquals( List.of(), service.list( tenant ) );
        assertEquals( List.of(), objects.listKeys( expired.location() ), "row gone AND prefix gone" );

        assertFalse( service.delete( tenant, expired.snapshotId() ), "a second delete is an idempotent no-op" );
        assertFalse( service.delete( tenant, "no-such-snapshot" ), "an unknown id is refused nothing either" );
        assertThrows( IllegalArgumentException.class, () -> service.delete( tenant, "../escape" ),
                      "a slash-carrying id is rejected before it reaches any prefix computation" );
    }

    // ------------------------------------------------------- search_document volume (Gate 0 #4)

    /**
     * MEASUREMENT, not just a test (Gate 0 unknown #4, assigned to Gate A): the
     * {@code search_document} COPY volume on a corpus-shaped tenant — nodes with realistic
     * XP-shipped documents (title/body with analyzed variants, tags, numerics, timestamps,
     * permissions), two branches — reported as bytes/row so Gate B (restore reload) and
     * Gate E (drill sizing) work from data. The numbers print on stdout with a MEASUREMENT
     * prefix; the assertions only pin the row count and a sanity floor.
     */
    @Test
    void searchDocumentCopyVolumeOnACorpusShapedTenantIsMeasured()
        throws Exception
    {
        TenantContext tenant = tenant( "tvol" );
        String repoId = createRepo( tenant, "volume" );
        int nodes = 300;

        // One batched write: 300 corpus-shaped nodes (versions + heads + payloads)...
        Tx.inTenantTx( dataSource, tenant, connection -> {
            List<PayloadRef> payloads = new ArrayList<>();
            List<VersionRecord> versions = new ArrayList<>();
            List<BranchEntryRecord> entries = new ArrayList<>();
            for ( int i = 0; i < nodes; i++ )
            {
                String content = "vol-" + i + "-" + corpusBody( i );
                payloads.add( new PayloadRef.Inline( ( "data-" + content ).getBytes( StandardCharsets.UTF_8 ) ) );
                payloads.add( new PayloadRef.Inline( ( "icfg-" + content ).getBytes( StandardCharsets.UTF_8 ) ) );
                payloads.add( new PayloadRef.Inline( ( "acl-" + content ).getBytes( StandardCharsets.UTF_8 ) ) );
                String nodeId = "article-" + i;
                versions.add( new VersionRecord( "v-" + nodeId, nodeId, "/content/articles/" + nodeId, Instant.now(),
                                                 sha( "data-" + content ), sha( "icfg-" + content ), sha( "acl-" + content ),
                                                 List.of(), null, Map.of() ) );
                entries.add( new BranchEntryRecord( "master", nodeId, "v-" + nodeId, "/content/articles/" + nodeId, Instant.now(),
                                                    null, null, null ) );
            }
            return WriteService.write( connection,
                                       new WriteBatchRequest( new RepoRef( repoId ), payloads, versions, entries, null ) );
        } );

        // ...and realistic shipped documents on BOTH branches (draft + master, as XP ships them).
        long repoKey = repoKey( tenant, repoId );
        Tx.inTenantTx( dataSource, tenant, connection -> {
            List<String> nodeIds = new ArrayList<>();
            for ( int i = 0; i < nodes; i++ )
            {
                String nodeId = "article-" + i;
                nodeIds.add( nodeId );
                SearchDocumentStore.store( connection, repoKey, "master", corpusDocument( nodeId, i ) );
                SearchDocumentStore.store( connection, repoKey, "draft", corpusDocument( nodeId, i ) );
            }
            OutboxStore.appendIndex( connection, repoKey, "master", nodeIds, null );
            OutboxStore.appendIndex( connection, repoKey, "draft", nodeIds, null );
            return null;
        } );

        SnapshotRecord snapshot = service.createRepoSnapshot( tenant, repoId );
        assertEquals( 2L * nodes, snapshot.documentCount() );

        JsonNode documentsArtifact = null;
        for ( JsonNode artifact : readManifest( snapshot ).path( "artifacts" ) )
        {
            if ( artifact.path( "key" ).asText().endsWith( "documents.copy.gz" ) )
            {
                documentsArtifact = artifact;
            }
        }
        assertNotNull( documentsArtifact );
        long rows = documentsArtifact.path( "rows" ).asLong();
        long gzipBytes = documentsArtifact.path( "bytes" ).asLong();
        byte[] raw;
        try (GZIPInputStream in = new GZIPInputStream( objects.get( snapshot.location() + documentsArtifact.path( "key" ).asText() ) ))
        {
            raw = in.readAllBytes();
        }

        assertEquals( 2L * nodes, rows );
        assertTrue( raw.length / rows > 200, "a corpus-shaped document row must not be trivially small — the measurement would be fake" );

        System.out.printf( "MEASUREMENT search_document COPY volume (corpus-shaped, %d nodes x 2 branches): rows=%d, "
                               + "raw=%d bytes (%.1f B/row), gzip=%d bytes (%.1f B/row), compression=%.2fx%n", nodes, rows, raw.length,
                           (double) raw.length / rows, gzipBytes, (double) gzipBytes / rows, (double) raw.length / gzipBytes );
        System.out.printf( "MEASUREMENT snapshot totals for the same repo: versions=%d heads=%d documents=%d hashes=%d totalBytes=%d%n",
                           snapshot.versionCount(), snapshot.headCount(), snapshot.documentCount(), snapshot.hashCount(),
                           snapshot.totalBytes() );
    }

    // -------------------------------------------------------------------------------- fixtures

    private static TenantContext tenant( String tenantId )
        throws SQLException
    {
        TenantContext tenant = new TenantContext( tenantId );
        provisioner.provision( tenant );
        return tenant;
    }

    private static String createRepo( TenantContext tenant, String prefix )
        throws SQLException
    {
        String repoId = prefix + "." + REPO_SEQUENCE.incrementAndGet();
        Tx.inTenantSchema( dataSource, tenant, connection -> {
            long repoKey = RepositoryLifecycle.createRepository( connection, repoId, null );
            RepositoryLifecycle.createBranch( connection, repoKey, "master" );
            RepositoryLifecycle.createBranch( connection, repoKey, "draft" );
            return null;
        } );
        return repoId;
    }

    private static long repoKey( TenantContext tenant, String repoId )
        throws SQLException
    {
        return Tx.inTenantTx( dataSource, tenant, connection -> RepoKeys.resolve( connection, new RepoRef( repoId ) ) );
    }

    /**
     * One node through the real write path ({@code WriteService.write}): three content
     * payloads, one version (id unique per branch so a two-branch seed makes two versions),
     * one branch entry, optionally a commit — the same shape XP's WriteBatch ships.
     */
    private static void seedNode( TenantContext tenant, String repoId, String branch, String nodeId, String content,
                                  List<String> binaryKeys, String commitId )
        throws SQLException
    {
        Tx.inTenantTx( dataSource, tenant, connection -> {
            List<PayloadRef> payloads = List.of( new PayloadRef.Inline( ( "data-" + content ).getBytes( StandardCharsets.UTF_8 ) ),
                                                 new PayloadRef.Inline( ( "icfg-" + content ).getBytes( StandardCharsets.UTF_8 ) ),
                                                 new PayloadRef.Inline( ( "acl-" + content ).getBytes( StandardCharsets.UTF_8 ) ) );
            String versionId = "v-" + nodeId + "-" + branch;
            VersionRecord version = new VersionRecord( versionId, nodeId, "/" + nodeId, Instant.now(), sha( "data-" + content ),
                                                       sha( "icfg-" + content ), sha( "acl-" + content ), binaryKeys, commitId,
                                                       Map.of() );
            BranchEntryRecord entry =
                new BranchEntryRecord( branch, nodeId, versionId, "/" + nodeId, Instant.now(), null, null, null );
            CommitRecord commit = commitId == null ? null : new CommitRecord( commitId, "seed", "tester", Instant.now() );
            return WriteService.write( connection, new WriteBatchRequest( new RepoRef( repoId ), payloads, List.of( version ),
                                                                          List.of( entry ), commit ) );
        } );
    }

    /** Ships one stored document + INDEX outbox row, the way {@code NodeSearchService.indexDocuments} does. */
    private static void shipDocument( TenantContext tenant, String repoId, String branch, String nodeId, String title )
        throws SQLException
    {
        long repoKey = repoKey( tenant, repoId );
        Map<String, List<SearchDocument.Value>> fields = new LinkedHashMap<>();
        fields.put( "data.title", List.of( new SearchDocument.Value.Text( title ) ) );
        fields.put( "_name", List.of( new SearchDocument.Value.Text( nodeId ) ) );
        SearchDocument document = new SearchDocument( nodeId, null, fields );
        Tx.inTenantTx( dataSource, tenant, connection -> {
            SearchDocumentStore.store( connection, repoKey, branch, document );
            return OutboxStore.appendIndex( connection, repoKey, branch, List.of( nodeId ), null );
        } );
    }

    /** A realistically shaped XP index document — what IndexDataService ships for an article node. */
    private static SearchDocument corpusDocument( String nodeId, int i )
    {
        String title = "Article " + i + ": the quick brown fox jumps over the lazy dog in sprint " + ( i % 17 );
        String body = corpusBody( i );
        Map<String, List<SearchDocument.Value>> fields = new LinkedHashMap<>();
        fields.put( "_name", List.of( new SearchDocument.Value.Text( nodeId ) ) );
        fields.put( "_path", List.of( new SearchDocument.Value.Text( "/content/articles/" + nodeId ) ) );
        fields.put( "type", List.of( new SearchDocument.Value.Text( "com.example.site:article" ) ) );
        fields.put( "_state", List.of( new SearchDocument.Value.Text( "DEFAULT" ) ) );
        fields.put( "createdtime", List.of( new SearchDocument.Value.Timestamp( 1_700_000_000_000L + i * 60_000L ) ) );
        fields.put( "modifiedtime", List.of( new SearchDocument.Value.Timestamp( 1_720_000_000_000L + i * 60_000L ) ) );
        fields.put( "owner", List.of( new SearchDocument.Value.Text( "user:system:editor-" + ( i % 7 ) ) ) );
        fields.put( "_permissions.read", List.of( new SearchDocument.Value.Text( "role:system.everyone" ),
                                                  new SearchDocument.Value.Text( "role:cms.cm.app" ),
                                                  new SearchDocument.Value.Text( "user:system:editor-" + ( i % 7 ) ) ) );
        fields.put( "data.title", List.of( new SearchDocument.Value.Text( title ) ) );
        fields.put( "data.title._analyzed", List.of( new SearchDocument.Value.Text( title ) ) );
        fields.put( "data.title._orderby", List.of( new SearchDocument.Value.Text( title.toLowerCase() ) ) );
        fields.put( "data.body", List.of( new SearchDocument.Value.Text( body ) ) );
        fields.put( "data.body._analyzed", List.of( new SearchDocument.Value.Text( body ) ) );
        fields.put( "data.tags", List.of( new SearchDocument.Value.Text( "tag-" + ( i % 5 ) ),
                                          new SearchDocument.Value.Text( "tag-" + ( i % 11 ) ),
                                          new SearchDocument.Value.Text( "evergreen" ) ) );
        fields.put( "data.likes", List.of( new SearchDocument.Value.Integer( i * 3L ) ) );
        fields.put( "data.rating", List.of( new SearchDocument.Value.Number( ( i % 50 ) / 10.0 ) ) );
        fields.put( "data.featured", List.of( new SearchDocument.Value.Bool( i % 4 == 0 ) ) );
        fields.put( "publish.from", List.of( new SearchDocument.Value.Timestamp( 1_710_000_000_000L + i * 3_600_000L ) ) );
        return new SearchDocument( nodeId, null, fields );
    }

    private static String corpusBody( int i )
    {
        StringBuilder body = new StringBuilder();
        for ( int p = 0; p < 3; p++ )
        {
            body.append( "Paragraph " ).append( p ).append( " of article " ).append( i ).append( ": " )
                .append( "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore " )
                .append( "et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut " )
                .append( "aliquip ex ea commodo consequat. Varied token " ).append( ( i * 31 + p * 7 ) % 1000 ).append( ". " );
        }
        return body.toString();
    }

    // --------------------------------------------------------------------------------- plumbing

    private static long maxSeq( TenantContext tenant )
        throws SQLException
    {
        return Tx.inTenantTx( dataSource, tenant, OutboxStore::maxSeq );
    }

    private static void setSnapshotHorizon( TenantContext tenant, String interval )
        throws SQLException
    {
        Tx.inTenantTx( dataSource, tenant, connection -> {
            try (Statement statement = connection.createStatement())
            {
                statement.execute( "UPDATE retention_policy SET snapshot_horizon = interval '" + interval + "'" );
            }
            return null;
        } );
    }

    /** Exact interval arithmetic, asserted by Postgres itself against the row's own created_at. */
    private static boolean expiryEquals( TenantContext tenant, String snapshotId, String interval )
        throws SQLException
    {
        return Tx.inTenantTx( dataSource, tenant, connection -> {
            try (PreparedStatement statement = connection.prepareStatement(
                "SELECT expires_at = created_at + interval '" + interval + "' FROM snapshot WHERE snapshot_id = ?" ))
            {
                statement.setString( 1, snapshotId );
                try (ResultSet resultSet = statement.executeQuery())
                {
                    return resultSet.next() && resultSet.getBoolean( 1 );
                }
            }
        } );
    }

    private static JsonNode readManifest( SnapshotRecord snapshot )
        throws IOException
    {
        return MAPPER.readTree( objects.get( snapshot.location() + "MANIFEST.json" ).readAllBytes() );
    }

    private static List<String> gunzipLines( String key )
        throws IOException
    {
        try (GZIPInputStream in = new GZIPInputStream( objects.get( key ) ))
        {
            String content = new String( in.readAllBytes(), StandardCharsets.UTF_8 );
            return content.isEmpty() ? List.of() : List.of( content.split( "\n" ) );
        }
    }

    private static String sha( String content )
    {
        return "sha256:" + sha256Hex( content.getBytes( StandardCharsets.UTF_8 ) );
    }

    private static String sha256Hex( byte[] bytes )
    {
        try
        {
            return HexFormat.of().formatHex( MessageDigest.getInstance( "SHA-256" ).digest( bytes ) );
        }
        catch ( java.security.NoSuchAlgorithmException e )
        {
            throw new IllegalStateException( e );
        }
    }

    /**
     * A {@link DataSource} whose connections run {@code hook} exactly once, immediately
     * BEFORE the first statement matching {@code query} is prepared — the same
     * proxied-DataSource machinery as IndexerTest's lost-write regressions, keyed on the
     * query (never a connection ordinal) so an unrelated extra round trip cannot silently
     * move the hook out of the window.
     */
    private static DataSource hookedBeforeQuery( DataSource delegate, Predicate<String> query, Runnable hook )
    {
        AtomicBoolean fired = new AtomicBoolean();
        return (DataSource) Proxy.newProxyInstance( SnapshotServiceTest.class.getClassLoader(), new Class<?>[]{DataSource.class},
                                                    ( dsProxy, dsMethod, dsArgs ) -> {
                                                        if ( !"getConnection".equals( dsMethod.getName() ) )
                                                        {
                                                            return invoke( delegate, dsMethod, dsArgs );
                                                        }
                                                        Connection real = (Connection) invoke( delegate, dsMethod, dsArgs );
                                                        return Proxy.newProxyInstance( SnapshotServiceTest.class.getClassLoader(),
                                                                                       new Class<?>[]{Connection.class},
                                                                                       ( cProxy, cMethod, cArgs ) -> {
                                                                                           if ( "prepareStatement".equals(
                                                                                               cMethod.getName() ) && cArgs != null &&
                                                                                               cArgs.length > 0 && query.test(
                                                                                               String.valueOf( cArgs[0] ) ) &&
                                                                                               fired.compareAndSet( false, true ) )
                                                                                           {
                                                                                               hook.run();
                                                                                           }
                                                                                           return invoke( real, cMethod, cArgs );
                                                                                       } );
                                                    } );
    }

    /** An {@link S3Client} that throws on its Nth {@code putObject} — the mid-create S3 outage. */
    private static S3Client failingOnNthPut( S3Client delegate, int failOn )
    {
        AtomicInteger puts = new AtomicInteger();
        return (S3Client) Proxy.newProxyInstance( SnapshotServiceTest.class.getClassLoader(), new Class<?>[]{S3Client.class},
                                                  ( proxy, method, args ) -> {
                                                      if ( "putObject".equals( method.getName() ) &&
                                                          puts.incrementAndGet() == failOn )
                                                      {
                                                          throw new UncheckedIOException(
                                                              new IOException( "injected S3 outage" ) );
                                                      }
                                                      return invoke( delegate, method, args );
                                                  } );
    }

    private static Object invoke( Object target, java.lang.reflect.Method method, Object[] args )
        throws Throwable
    {
        try
        {
            return method.invoke( target, args );
        }
        catch ( InvocationTargetException e )
        {
            throw e.getCause();
        }
    }
}
