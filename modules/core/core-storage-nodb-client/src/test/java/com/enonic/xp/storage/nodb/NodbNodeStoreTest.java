package com.enonic.xp.storage.nodb;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.Server;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.ServerInterceptors;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.storage.spi.BranchEntryListing;
import com.enonic.xp.storage.spi.BranchEntryRecord;
import com.enonic.xp.storage.spi.CommitRecord;
import com.enonic.xp.storage.spi.NodeSegments;
import com.enonic.xp.storage.spi.PayloadSegment;
import com.enonic.xp.storage.spi.StorageIndexNotFoundException;
import com.enonic.xp.storage.spi.VersionQuery;
import com.enonic.xp.storage.spi.VersionQueryResult;
import com.enonic.xp.storage.spi.VersionRecord;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link NodbNodeStore} against an in-process stub server (see {@link FakeNodbState}'s
 * javadoc for why): status-mapping (NOT_FOUND -&gt; null for point-gets, NOT_FOUND -&gt;
 * {@link StorageIndexNotFoundException} for repo-scoped ops), the branch-entry/version join
 * (nodeDataHash recovery), attribute round-tripping, bearer-token attachment, and (Phase 3
 * Gate B, nodb/BUILD-PHASE-3.md) the payload-segment/{@code WriteBatch} plumbing:
 * {@link #storeVersion} and {@link #storeNode} carrying inline segment bytes, hash-only
 * reuse, and the {@code NEED_PAYLOAD} failure mode.
 */
class NodbNodeStoreTest
{
    private static final RepositoryId REPO = RepositoryId.from( "myrepo" );

    private static final Branch BRANCH = Branch.from( "master" );

    /**
     * Placeholder segments for tests that are about version/branch-entry/proto mapping, not
     * the payload mechanism itself: always inline (never hash-only), so the stub's
     * {@code WriteBatch} never returns {@code NEED_PAYLOAD} regardless of what the test's
     * {@code VersionRecord} hash fields happen to be (the two are deliberately decoupled in
     * {@link StubNodeStoreService} -- see its {@code writeBatch} javadoc).
     */
    private static final NodeSegments TEST_SEGMENTS =
        new NodeSegments( new PayloadSegment( "sha256:node-placeholder", "node".getBytes( StandardCharsets.UTF_8 ) ),
                           new PayloadSegment( "sha256:index-placeholder", "index".getBytes( StandardCharsets.UTF_8 ) ),
                           new PayloadSegment( "sha256:access-placeholder", "access".getBytes( StandardCharsets.UTF_8 ) ) );

    private FakeNodbState state;

    private Server server;

    private ManagedChannel channel;

    private NodbNodeStore nodeStore;

    private final AtomicReference<String> capturedAuthHeader = new AtomicReference<>();

    @BeforeEach
    void setUp()
        throws Exception
    {
        state = new FakeNodbState();
        state.repos.add( REPO.toString() );

        final String serverName = "nodb-node-store-test-" + System.nanoTime();
        final Metadata.Key<String> authKey = Metadata.Key.of( "authorization", Metadata.ASCII_STRING_MARSHALLER );
        final ServerInterceptor capturingInterceptor = new ServerInterceptor()
        {
            @Override
            public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall( final ServerCall<ReqT, RespT> call, final Metadata headers,
                                                                           final ServerCallHandler<ReqT, RespT> next )
            {
                capturedAuthHeader.set( headers.get( authKey ) );
                return next.startCall( call, headers );
            }
        };

        server = InProcessServerBuilder.forName( serverName )
            .directExecutor()
            .addService( ServerInterceptors.intercept( new StubNodeStoreService( state ), capturingInterceptor ) )
            .build()
            .start();
        channel = InProcessChannelBuilder.forName( serverName ).directExecutor().build();
        nodeStore = new NodbNodeStore( new InProcessNodbStorageClient( channel, "test-token-123" ) );
    }

    @AfterEach
    void tearDown()
    {
        channel.shutdownNow();
        server.shutdownNow();
    }

    @Test
    void storeAndGetBranchEntry_joinsVersionForHashFields()
    {
        final Instant now = Instant.now();
        final VersionRecord version =
            new VersionRecord( "v1", "n1", "/a/b", now, "sha256:data", "sha256:idx", "sha256:acl", List.of(), null, null );
        nodeStore.storeVersion( REPO, version, TEST_SEGMENTS );

        final BranchEntryRecord entry = new BranchEntryRecord( "n1", "/a/b", "v1", "sha256:data", "sha256:idx", "sha256:acl", now );
        nodeStore.storeBranchEntry( REPO, BRANCH, entry );

        final BranchEntryRecord fetched = nodeStore.getBranchEntry( REPO, BRANCH, "n1", null );
        assertEquals( "n1", fetched.nodeId() );
        assertEquals( "/a/b", fetched.nodePath() );
        assertEquals( "v1", fetched.versionId() );
        assertEquals( "sha256:data", fetched.nodeDataHash() );
        assertEquals( "sha256:idx", fetched.indexConfigHash() );
        assertEquals( "sha256:acl", fetched.aclHash() );

        final BranchEntryRecord byPath = nodeStore.getBranchEntryByPath( REPO, BRANCH, "/a/b", null );
        assertEquals( "n1", byPath.nodeId() );
    }

    @Test
    void getBranchEntry_notFound_returnsNull()
    {
        assertNull( nodeStore.getBranchEntry( REPO, BRANCH, "missing", null ) );
        assertNull( nodeStore.getBranchEntryByPath( REPO, BRANCH, "/no/such", null ) );
    }

    @Test
    void getVersion_notFound_returnsNull()
    {
        assertNull( nodeStore.getVersion( REPO, "no-such-version", null ) );
    }

    @Test
    void getCommit_notFound_returnsNull()
    {
        assertNull( nodeStore.getCommit( REPO, "no-such-commit", null ) );
    }

    @Test
    void repoScopedOp_unknownRepo_throwsStorageIndexNotFoundException()
    {
        final RepositoryId unknown = RepositoryId.from( "unknown" );
        final VersionRecord version =
            new VersionRecord( "v2", "n2", "/x", Instant.now(), "sha256:x", null, null, List.of(), null, null );
        assertThrows( StorageIndexNotFoundException.class, () -> nodeStore.storeVersion( unknown, version, TEST_SEGMENTS ) );
    }

    @Test
    void existsBranchEntry_trueAndFalse()
    {
        assertFalse( nodeStore.existsBranchEntry( REPO, BRANCH, "n3", null ) );

        final VersionRecord version = new VersionRecord( "v3", "n3", "/c", Instant.now(), "sha256:c", null, null, List.of(), null, null );
        nodeStore.storeVersion( REPO, version, TEST_SEGMENTS );
        nodeStore.storeBranchEntry( REPO, BRANCH, new BranchEntryRecord( "n3", "/c", "v3", "sha256:c", null, null, Instant.now() ) );

        assertTrue( nodeStore.existsBranchEntry( REPO, BRANCH, "n3", null ) );
    }

    @Test
    void getBranchEntries_multiGet_missingIdsAreSimplyAbsent()
    {
        final VersionRecord v = new VersionRecord( "v4", "n4", "/d", Instant.now(), "sha256:d", null, null, List.of(), null, null );
        nodeStore.storeVersion( REPO, v, TEST_SEGMENTS );
        nodeStore.storeBranchEntry( REPO, BRANCH, new BranchEntryRecord( "n4", "/d", "v4", "sha256:d", null, null, Instant.now() ) );

        final List<BranchEntryRecord> result = nodeStore.getBranchEntries( REPO, BRANCH, List.of( "n4", "does-not-exist" ), null );
        assertEquals( 1, result.size() );
        assertEquals( "n4", result.get( 0 ).nodeId() );
    }

    @Test
    void getChildren_ordersByPathAndPaginates()
    {
        // Phase 1 Gate C SPI addition (NodeStore#getChildren, nodb-only): storage-side
        // children listing via the server's parent_path JOIN, no NodeSearchIndex involved.
        for ( final String name : List.of( "b", "a", "c" ) )
        {
            final VersionRecord v =
                new VersionRecord( "v-" + name, "n-" + name, "/" + name, Instant.now(), "sha256:" + name, null, null, List.of(), null,
                                    null );
            nodeStore.storeVersion( REPO, v, TEST_SEGMENTS );
            nodeStore.storeBranchEntry( REPO, BRANCH, new BranchEntryRecord( "n-" + name, "/" + name, "v-" + name, "sha256:" + name, null,
                                                                              null, Instant.now() ) );
        }

        final List<BranchEntryRecord> children = nodeStore.getChildren( REPO, BRANCH, "/", 0, 10, null );
        assertEquals( List.of( "/a", "/b", "/c" ), children.stream().map( BranchEntryRecord::nodePath ).toList() );

        final List<BranchEntryRecord> page = nodeStore.getChildren( REPO, BRANCH, "/", 1, 1, null );
        assertEquals( List.of( "/b" ), page.stream().map( BranchEntryRecord::nodePath ).toList() );
    }

    @Test
    void getBranchesWithNode_returnsAllBranchesContainingNode()
    {
        final VersionRecord v = new VersionRecord( "v5", "n5", "/e", Instant.now(), "sha256:e", null, null, List.of(), null, null );
        nodeStore.storeVersion( REPO, v, TEST_SEGMENTS );
        nodeStore.storeBranchEntry( REPO, BRANCH, new BranchEntryRecord( "n5", "/e", "v5", "sha256:e", null, null, Instant.now() ) );
        nodeStore.storeBranchEntry( REPO, Branch.from( "draft" ),
                                     new BranchEntryRecord( "n5", "/e", "v5", "sha256:e", null, null, Instant.now() ) );

        final List<Branch> branches = nodeStore.getBranchesWithNode( REPO, "n5" );
        assertEquals( Set.of( Branch.from( "master" ), Branch.from( "draft" ) ), Set.copyOf( branches ) );
    }

    @Test
    void versionAttributes_roundTripThroughJsonCodec()
    {
        final Map<String, Object> attributes = Map.of( "count", 42, "label", "hello", "nested", Map.of( "a", 1 ) );
        final VersionRecord version =
            new VersionRecord( "v6", "n6", "/f", Instant.now(), "sha256:f", null, null, List.of( "sha256:bin1" ), "c1", attributes );
        nodeStore.storeVersion( REPO, version, TEST_SEGMENTS );

        final VersionRecord fetched = nodeStore.getVersion( REPO, "v6", null );
        assertEquals( "c1", fetched.commitId() );
        assertEquals( List.of( "sha256:bin1" ), fetched.binaryKeys() );
        assertEquals( 42, fetched.attributes().get( "count" ) );
        assertEquals( "hello", fetched.attributes().get( "label" ) );
    }

    @Test
    void versionWithNoAttributes_roundTripsAsNull()
    {
        final VersionRecord version = new VersionRecord( "v7", "n7", "/g", Instant.now(), "sha256:g", null, null, List.of(), null, null );
        nodeStore.storeVersion( REPO, version, TEST_SEGMENTS );

        assertNull( nodeStore.getVersion( REPO, "v7", null ).attributes() );
    }

    @Test
    void commit_roundTrip_emptyStringsBecomeNull()
    {
        final CommitRecord withNulls = new CommitRecord( "commit1", null, null, Instant.now() );
        nodeStore.storeCommit( REPO, withNulls );

        final CommitRecord fetched = nodeStore.getCommit( REPO, "commit1", null );
        assertNull( fetched.message() );
        assertNull( fetched.committer() );
    }

    @Test
    void everyCallCarriesTheBearerToken()
    {
        nodeStore.existsBranchEntry( REPO, BRANCH, "n1", null );
        assertEquals( "Bearer test-token-123", capturedAuthHeader.get() );
    }

    @Test
    void storeVersion_sendsInlineSegmentBytes_retrievableViaGetPayload()
    {
        final PayloadSegment nodeData = new PayloadSegment( "sha256:node-inline", "node-bytes".getBytes( StandardCharsets.UTF_8 ) );
        final PayloadSegment indexConfig = new PayloadSegment( "sha256:index-inline", "index-bytes".getBytes( StandardCharsets.UTF_8 ) );
        final PayloadSegment accessControl = new PayloadSegment( "sha256:acl-inline", "acl-bytes".getBytes( StandardCharsets.UTF_8 ) );
        final NodeSegments segments = new NodeSegments( nodeData, indexConfig, accessControl );

        final VersionRecord version =
            new VersionRecord( "v8", "n8", "/h", Instant.now(), "sha256:h", "sha256:hidx", "sha256:hacl", List.of(), null, null );
        nodeStore.storeVersion( REPO, version, segments );

        // The stub recomputes the hash from the inline bytes server-side (never trusts the
        // client) -- assert the CONTENT landed, keyed by that recomputed hash, not by
        // whatever placeholder hash string the PayloadSegment carried.
        assertEquals( "node-bytes",
                       state.payloads.get( BlobKey.sha256( ByteSource.wrap(
                           "node-bytes".getBytes( StandardCharsets.UTF_8 ) ) ).toString() ).toStringUtf8() );
    }

    @Test
    void storeVersion_hashOnlySegment_unknownHash_reportsNeedPayloadAsClientException()
    {
        // Mirrors VersionServiceImpl's commit/change-attributes convenience overload:
        // hash-only segments reusing a key the server does NOT actually have yet.
        final NodeSegments hashOnly =
            new NodeSegments( new PayloadSegment( "sha256:never-stored-1", null ), new PayloadSegment( "sha256:never-stored-2", null ),
                               new PayloadSegment( "sha256:never-stored-3", null ) );
        final VersionRecord version =
            new VersionRecord( "v9", "n9", "/i", Instant.now(), "sha256:never-stored-1", "sha256:never-stored-2",
                                "sha256:never-stored-3", List.of(), null, null );

        assertThrows( NodbClientException.class, () -> nodeStore.storeVersion( REPO, version, hashOnly ) );
        // Nothing partially persisted: WriteService's real pre-check ordering (validate all
        // hash-only refs before writing anything) is mirrored by the stub's own ordering.
        assertNull( nodeStore.getVersion( REPO, "v9", null ) );
    }

    @Test
    void storeVersion_hashOnlySegment_knownHash_succeeds()
    {
        // First write: inline, populates state.payloads under the recomputed hash.
        final String contentHash =
            BlobKey.sha256( ByteSource.wrap( "shared".getBytes( StandardCharsets.UTF_8 ) ) )
                .toString();
        final NodeSegments inline = new NodeSegments( new PayloadSegment( contentHash, "shared".getBytes( StandardCharsets.UTF_8 ) ),
                                                        new PayloadSegment( contentHash, "shared".getBytes( StandardCharsets.UTF_8 ) ),
                                                        new PayloadSegment( contentHash, "shared".getBytes( StandardCharsets.UTF_8 ) ) );
        nodeStore.storeVersion( REPO, new VersionRecord( "v10", "n10", "/j", Instant.now(), contentHash, contentHash, contentHash,
                                                          List.of(), null, null ), inline );

        // Second write (e.g. commit/change-attributes reusing the same key): hash-only,
        // referencing content already stored by the first write above -- must succeed.
        final NodeSegments hashOnly = new NodeSegments( new PayloadSegment( contentHash, null ), new PayloadSegment( contentHash, null ),
                                                          new PayloadSegment( contentHash, null ) );
        nodeStore.storeVersion( REPO, new VersionRecord( "v11", "n11", "/k", Instant.now(), contentHash, contentHash, contentHash,
                                                          List.of(), null, null ), hashOnly );

        assertEquals( "v11", nodeStore.getVersion( REPO, "v11", null ).versionId() );
    }

    @Test
    void versionGetAndDeleteAreRepoScoped_sameVersionIdInTwoRepos()
    {
        // Phase 3.5 gate P2: version identity is (repo, version_id) on the wire — the SAME
        // version_id string stored in TWO repos must stay independent for get and delete.
        final RepositoryId repoB = RepositoryId.from( "otherrepo" );
        state.repos.add( repoB.toString() );

        nodeStore.storeVersion( REPO, new VersionRecord( "v-shared", "nA", "/p2-a", Instant.now(), "sha256:a", null, null, List.of(),
                                                          null, null ), TEST_SEGMENTS );
        nodeStore.storeVersion( repoB, new VersionRecord( "v-shared", "nB", "/p2-b", Instant.now(), "sha256:b", null, null, List.of(),
                                                           null, null ), TEST_SEGMENTS );

        assertEquals( "/p2-a", nodeStore.getVersion( REPO, "v-shared", null ).nodePath() );
        assertEquals( "/p2-b", nodeStore.getVersion( repoB, "v-shared", null ).nodePath() );

        nodeStore.deleteVersion( REPO, "v-shared" );

        assertNull( nodeStore.getVersion( REPO, "v-shared", null ) );
        assertEquals( "/p2-b", nodeStore.getVersion( repoB, "v-shared", null ).nodePath() );
    }

    @Test
    void supportsBranchEntryQueries_capabilityProbeIsTrue()
    {
        assertTrue( nodeStore.supportsBranchEntryQueries(),
                    "capability probe must be true so the delete cascade and reindex can route without a config lookup" );
    }

    /**
     * Phase 4 decision D2: the listing is a keyset-paged iterable behind an up-front total. What
     * this asserts is the CLIENT half of that — that the iterator keeps fetching pages until the
     * server says there are no more, and that {@code totalHits} is available before any entry is
     * consumed. The page size is 1000, so the fixture cannot cross a real page boundary; the
     * boundary arithmetic itself is covered against a real database by the engine's
     * {@code BranchEntryListingTest}.
     */
    @Test
    void listChildEntries_isPathDescendingSubtreeWithAnUpFrontTotal()
    {
        storeEntry( BRANCH, "n-content", "v-content", "/content" );
        storeEntry( BRANCH, "n-a", "v-a", "/content/a" );
        storeEntry( BRANCH, "n-deep", "v-deep", "/content/a/deep" );
        storeEntry( BRANCH, "n-b", "v-b", "/content/b" );
        storeEntry( BRANCH, "n-sibling", "v-sibling", "/content-sibling" );

        final BranchEntryListing listing = nodeStore.listChildEntries( REPO, BRANCH, "/content" );

        assertEquals( 3, listing.totalHits(), "the total must be known before the first entry is consumed" );
        assertEquals( List.of( "/content/b", "/content/a/deep", "/content/a" ), paths( listing ) );
    }

    @Test
    void listBranchEntries_isTheWholeBranchAscendingAndPerBranch()
    {
        final Branch draft = Branch.from( "draft" );
        storeEntry( BRANCH, "n-content", "v-content", "/content" );
        storeEntry( BRANCH, "n-a", "v-a", "/content/a" );
        storeEntry( draft, "n-draft-only", "v-draft-only", "/content/draft-only" );

        final BranchEntryListing listing = nodeStore.listBranchEntries( REPO, BRANCH );
        assertEquals( 2, listing.totalHits() );
        assertEquals( List.of( "/content", "/content/a" ), paths( listing ) );

        assertEquals( List.of( "/content/draft-only" ), paths( nodeStore.listBranchEntries( REPO, draft ) ) );
    }

    private static List<String> paths( final BranchEntryListing listing )
    {
        final List<String> paths = new java.util.ArrayList<>();
        listing.entries().forEach( entry -> paths.add( entry.nodePath() ) );
        return paths;
    }

    @Test
    void supportsVersionQueries_capabilityProbeIsTrue()
    {
        assertTrue( nodeStore.supportsVersionQueries(), "capability probe must be true so Gate B commands can route without config lookups" );
    }

    @Test
    void findVersions_historyOrderingTotalHitsAndKeysetCursor()
    {
        // Four versions of one node; two share a timestamp so the version_id ASC
        // tiebreaker is observable in the ts DESC ordering.
        storeVersionAt( "vh-1", "nh", 1_000 );
        storeVersionAt( "vh-2a", "nh", 2_000 );
        storeVersionAt( "vh-2b", "nh", 2_000 );
        storeVersionAt( "vh-3", "nh", 3_000 );

        final VersionQueryResult page1 = nodeStore.findVersions( REPO,
                                                                  new VersionQuery( "nh", null, null, null, null, null,
                                                                                     VersionQuery.Order.TS_DESC_ID_ASC, 0, 2 ) );
        assertEquals( 4, page1.totalHits(), "totalHits must be accurate independent of page size" );
        assertEquals( List.of( "vh-3", "vh-2a" ), page1.versions().stream().map( VersionRecord::versionId ).toList() );

        final VersionQueryResult continuation = nodeStore.findVersions( REPO, new VersionQuery( "nh", null, null, null, null,
                                                                                                  new VersionQuery.Cursor(
                                                                                                      Instant.ofEpochMilli( 2_000 ),
                                                                                                      "vh-2a" ),
                                                                                                  VersionQuery.Order.TS_DESC_ID_ASC, 0,
                                                                                                  -1 ) );
        assertEquals( List.of( "vh-2b", "vh-1" ), continuation.versions().stream().map( VersionRecord::versionId ).toList(),
                      "keyset cursor must continue strictly after the last-seen (ts, version_id) with no overlap or skip" );
    }

    @Test
    void findVersions_countOnlyAndBlobKeyTerms()
    {
        final VersionRecord withBinary = new VersionRecord( "vb-1", "nb", "/vb", Instant.ofEpochMilli( 1_000 ), "sha256:vb-data", null,
                                                             null, List.of( "s3-bin-1" ), null, null );
        nodeStore.storeVersion( REPO, withBinary, TEST_SEGMENTS );
        final VersionRecord withoutBinary = new VersionRecord( "vb-2", "nb", "/vb", Instant.ofEpochMilli( 2_000 ), "sha256:vb-other",
                                                                null, null, List.of(), null, null );
        nodeStore.storeVersion( REPO, withoutBinary, TEST_SEGMENTS );

        final VersionQueryResult binaryCount = nodeStore.findVersions( REPO, new VersionQuery( null, null, null, null,
                                                                                                 new VersionQuery.BlobKeyTerm( "s3-bin-1",
                                                                                                                                VersionQuery.BlobKeyField.BINARY_KEYS ),
                                                                                                 null, VersionQuery.Order.UNORDERED, 0,
                                                                                                 0 ) );
        assertEquals( 1, binaryCount.totalHits() );
        assertTrue( binaryCount.versions().isEmpty(), "size 0 must be count-only" );

        final VersionQueryResult byDataHash = nodeStore.findVersions( REPO, new VersionQuery( null, null, null, null,
                                                                                                new VersionQuery.BlobKeyTerm(
                                                                                                    "sha256:vb-other",
                                                                                                    VersionQuery.BlobKeyField.NODE_DATA_HASH ),
                                                                                                null, VersionQuery.Order.UNORDERED, 0,
                                                                                                -1 ) );
        assertEquals( List.of( "vb-2" ), byDataHash.versions().stream().map( VersionRecord::versionId ).toList() );
    }

    @Test
    void diffBranches_returnsDistinctNodeIdsHonorsScopeAndLimit()
    {
        final Branch draft = Branch.from( "draft" );
        storeEntry( BRANCH, "n-both-diff", "v-old", "/a/both-diff" );
        storeEntry( draft, "n-both-diff", "v-new", "/a/both-diff" );
        storeEntry( BRANCH, "n-same", "v-same", "/a/same" );
        storeEntry( draft, "n-same", "v-same", "/a/same" );
        storeEntry( draft, "n-draft-only", "v-d", "/a/draft-only" );
        storeEntry( draft, "n-outside", "v-o", "/elsewhere/outside" );

        final List<String> all = nodeStore.diffBranches( REPO, draft, BRANCH, null, List.of(), 0 );
        assertEquals( Set.of( "n-both-diff", "n-draft-only", "n-outside" ), Set.copyOf( all ),
                      "both-with-different-versions dedups to one id; same-version is absent" );

        final List<String> scoped = nodeStore.diffBranches( REPO, draft, BRANCH, "/A", List.of(), 0 );
        assertEquals( Set.of( "n-both-diff", "n-draft-only" ), Set.copyOf( scoped ), "scope is case-insensitive" );

        final List<String> limited = nodeStore.diffBranches( REPO, draft, BRANCH, "/a", List.of(), 1 );
        assertEquals( 1, limited.size(), "limit 1 is the existence-only probe" );
    }

    @Test
    void getActiveVersions_returnsVersionPerBranchWhereNodeExists()
    {
        final Branch draft = Branch.from( "draft" );
        final VersionRecord masterVersion =
            new VersionRecord( "av-m", "n-av", "/av", Instant.ofEpochMilli( 1_000 ), "sha256:av-m", null, null, List.of(), null, null );
        final VersionRecord draftVersion =
            new VersionRecord( "av-d", "n-av", "/av", Instant.ofEpochMilli( 2_000 ), "sha256:av-d", null, null, List.of(), null, null );
        nodeStore.storeVersion( REPO, masterVersion, TEST_SEGMENTS );
        nodeStore.storeVersion( REPO, draftVersion, TEST_SEGMENTS );
        storeEntry( BRANCH, "n-av", "av-m", "/av" );
        storeEntry( draft, "n-av", "av-d", "/av" );

        final Map<Branch, VersionRecord> active =
            nodeStore.getActiveVersions( REPO, "n-av", List.of( BRANCH, draft, Branch.from( "no-such-branch" ) ) );
        assertEquals( 2, active.size(), "a branch without the node must simply be absent" );
        assertEquals( "av-m", active.get( BRANCH ).versionId() );
        assertEquals( "av-d", active.get( draft ).versionId() );
    }

    @Test
    void findCommits_returnsOnlyThisReposCommits()
    {
        final RepositoryId repoB = RepositoryId.from( "otherrepo" );
        state.repos.add( repoB.toString() );

        nodeStore.storeCommit( REPO, new CommitRecord( "fc-1", "first", "user:a", Instant.ofEpochMilli( 1_000 ) ) );
        nodeStore.storeCommit( REPO, new CommitRecord( "fc-2", "second", "user:a", Instant.ofEpochMilli( 2_000 ) ) );
        nodeStore.storeCommit( repoB, new CommitRecord( "fc-other", "other repo", "user:b", Instant.ofEpochMilli( 1_500 ) ) );

        final List<CommitRecord> commits = nodeStore.findCommits( REPO );
        assertEquals( List.of( "fc-1", "fc-2" ), commits.stream().map( CommitRecord::commitId ).toList() );
    }

    @Test
    void getCommit_isRepoScoped()
    {
        final RepositoryId repoB = RepositoryId.from( "otherrepo" );
        state.repos.add( repoB.toString() );

        nodeStore.storeCommit( REPO, new CommitRecord( "gc-scoped", "mine", "user:a", Instant.ofEpochMilli( 1_000 ) ) );

        assertEquals( "mine", nodeStore.getCommit( REPO, "gc-scoped", null ).message() );
        assertNull( nodeStore.getCommit( repoB, "gc-scoped", null ), "a commit is not addressable through another repo" );
    }

    private void storeVersionAt( final String versionId, final String nodeId, final long tsMillis )
    {
        nodeStore.storeVersion( REPO, new VersionRecord( versionId, nodeId, "/" + nodeId, Instant.ofEpochMilli( tsMillis ),
                                                          "sha256:" + versionId, null, null, List.of(), null, null ), TEST_SEGMENTS );
    }

    private void storeEntry( final Branch branch, final String nodeId, final String versionId, final String nodePath )
    {
        nodeStore.storeVersion( REPO, new VersionRecord( versionId, nodeId, nodePath, Instant.ofEpochMilli( 1_000 ),
                                                          "sha256:" + versionId, null, null, List.of(), null, null ), TEST_SEGMENTS );
        nodeStore.storeBranchEntry( REPO, branch,
                                     new BranchEntryRecord( nodeId, nodePath, versionId, "sha256:" + versionId, null, null,
                                                             Instant.ofEpochMilli( 1_000 ) ) );
    }

    @Test
    void storeNode_oneCallStoresVersionAndBranchEntryTogether()
    {
        final VersionRecord version =
            new VersionRecord( "v12", "n12", "/m", Instant.now(), "sha256:m", "sha256:midx", "sha256:macl", List.of(), null, null );
        final BranchEntryRecord entry = new BranchEntryRecord( "n12", "/m", "v12", "sha256:m", "sha256:midx", "sha256:macl",
                                                                 Instant.now() );

        nodeStore.storeNode( REPO, BRANCH, TEST_SEGMENTS, version, entry );

        assertEquals( "v12", nodeStore.getVersion( REPO, "v12", null ).versionId() );
        assertEquals( "n12", nodeStore.getBranchEntry( REPO, BRANCH, "n12", null ).nodeId() );
    }
}
