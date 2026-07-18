package com.enonic.xp.storage.nodb;

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

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.storage.spi.BranchEntryRecord;
import com.enonic.xp.storage.spi.CommitRecord;
import com.enonic.xp.storage.spi.StorageIndexNotFoundException;
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
 * (nodeDataHash recovery), attribute round-tripping, and bearer-token attachment.
 */
class NodbNodeStoreTest
{
    private static final RepositoryId REPO = RepositoryId.from( "myrepo" );

    private static final Branch BRANCH = Branch.from( "master" );

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
        nodeStore.storeVersion( REPO, version );

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
        assertThrows( StorageIndexNotFoundException.class, () -> nodeStore.storeVersion( unknown, version ) );
    }

    @Test
    void existsBranchEntry_trueAndFalse()
    {
        assertFalse( nodeStore.existsBranchEntry( REPO, BRANCH, "n3", null ) );

        final VersionRecord version = new VersionRecord( "v3", "n3", "/c", Instant.now(), "sha256:c", null, null, List.of(), null, null );
        nodeStore.storeVersion( REPO, version );
        nodeStore.storeBranchEntry( REPO, BRANCH, new BranchEntryRecord( "n3", "/c", "v3", "sha256:c", null, null, Instant.now() ) );

        assertTrue( nodeStore.existsBranchEntry( REPO, BRANCH, "n3", null ) );
    }

    @Test
    void getBranchEntries_multiGet_missingIdsAreSimplyAbsent()
    {
        final VersionRecord v = new VersionRecord( "v4", "n4", "/d", Instant.now(), "sha256:d", null, null, List.of(), null, null );
        nodeStore.storeVersion( REPO, v );
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
            nodeStore.storeVersion( REPO, v );
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
        nodeStore.storeVersion( REPO, v );
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
        nodeStore.storeVersion( REPO, version );

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
        nodeStore.storeVersion( REPO, version );

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
}
