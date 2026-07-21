package com.enonic.xp.repo.impl.storage.branch;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobKeys;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.repo.impl.branch.storage.BranchServiceImpl;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.storage.spi.BranchEntryRecord;
import com.enonic.xp.storage.spi.NodeSegments;
import com.enonic.xp.storage.spi.NodeStore;
import com.enonic.xp.storage.spi.PayloadSegment;
import com.enonic.xp.storage.spi.VersionRecord;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

class BranchServiceImplTest
{
    private BranchServiceImpl branchService;

    private NodeStore nodeStore;

    @BeforeEach
    void setup()
    {
        this.nodeStore = Mockito.mock( NodeStore.class );

        this.branchService = new BranchServiceImpl( nodeStore );
    }

    @Test
    void path_fetched_from_cache_after_stored()
    {
        final InternalContext context = InternalContext.create()
            .branch( Branch.from( "my-branch" ) )
            .repositoryId( RepositoryId.from( "my-repo" ) )
            .principalsKeys( AuthenticationInfo.unAuthenticated().getPrincipals() )
            .build();

        final NodePath path = new NodePath( "/fisk" );

        this.branchService.store( NodeBranchEntry.create()
                                      .nodeId( NodeId.from( "123" ) )
                                      .nodePath( path )
                                      .nodeVersionId( NodeVersionId.from( "nodeversionid" ) )
                                      .nodeVersionKey( NodeVersionKey.create()
                                                           .nodeBlobKey( BlobKey.from( "nodeBlobKey" ) )
                                                           .indexConfigBlobKey( BlobKey.from( "indexConfigBlobKey" ) )
                                                           .accessControlBlobKey( BlobKey.from( "accessControlBlobKey" ) )
                                                           .build() )
                                      .timestamp( Instant.now() )
                                      .build(), context );

        Mockito.when( this.nodeStore.getBranchEntry( Mockito.eq( context.getRepositoryId() ), Mockito.eq( context.getBranch() ),
                                                       Mockito.eq( "123" ), Mockito.any() ) )
            .thenReturn( new BranchEntryRecord( "123", "/fisk", "nodeversionid", "nodeBlobKey", "indexConfigBlobKey",
                                                 "accessControlBlobKey", Instant.now() ) );

        Mockito.when( this.nodeStore.getBranchEntryByPath( Mockito.eq( context.getRepositoryId() ), Mockito.eq( context.getBranch() ),
                                                            Mockito.eq( path.toString() ), Mockito.any() ) ).thenReturn( null );

        final NodeBranchEntry fetchEntry = this.branchService.get( path, context );

        assertNotNull( fetchEntry );
    }

    /**
     * Phase 3 Gate B (nodb/BUILD-PHASE-3.md's "ONE WriteBatch per save"): the combined
     * save path must issue exactly ONE {@code NodeStore#storeNode} call carrying the
     * version, branch entry AND segments together -- not a separate
     * {@code storeVersion}/{@code storeBranchEntry} pair.
     */
    @Test
    void storeWithVersion_issuesOneCombinedNodeStoreCall()
    {
        final InternalContext context = InternalContext.create()
            .branch( Branch.from( "my-branch" ) )
            .repositoryId( RepositoryId.from( "my-repo" ) )
            .principalsKeys( AuthenticationInfo.unAuthenticated().getPrincipals() )
            .build();

        final NodeVersionKey key = NodeVersionKey.create()
            .nodeBlobKey( BlobKey.from( "sha256:node" ) )
            .indexConfigBlobKey( BlobKey.from( "sha256:index" ) )
            .accessControlBlobKey( BlobKey.from( "sha256:access" ) )
            .build();

        final NodeBranchEntry nodeBranchEntry = NodeBranchEntry.create()
            .nodeId( NodeId.from( "456" ) )
            .nodePath( new NodePath( "/torsk" ) )
            .nodeVersionId( NodeVersionId.from( "v-456" ) )
            .nodeVersionKey( key )
            .timestamp( Instant.now() )
            .build();

        final NodeVersion nodeVersion = NodeVersion.create()
            .nodeId( NodeId.from( "456" ) )
            .nodeVersionId( NodeVersionId.from( "v-456" ) )
            .nodeVersionKey( key )
            .binaryBlobKeys( BlobKeys.empty() )
            .nodePath( new NodePath( "/torsk" ) )
            .timestamp( Instant.now() )
            .build();

        final NodeSegments segments = new NodeSegments( new PayloadSegment( "sha256:node", "node-bytes".getBytes() ),
                                                          new PayloadSegment( "sha256:index", "index-bytes".getBytes() ),
                                                          new PayloadSegment( "sha256:access", "access-bytes".getBytes() ) );

        this.branchService.storeWithVersion( nodeBranchEntry, nodeVersion, segments, context );

        verify( this.nodeStore ).storeNode( Mockito.eq( context.getRepositoryId() ), Mockito.eq( context.getBranch() ),
                                             Mockito.eq( segments ), Mockito.any( VersionRecord.class ),
                                             Mockito.any( BranchEntryRecord.class ) );
        Mockito.verifyNoMoreInteractions( this.nodeStore );
    }

    @Test
    void storeWithVersion_pathAlreadyStoredForDifferentNode_throws()
    {
        final InternalContext context = InternalContext.create()
            .branch( Branch.from( "my-branch" ) )
            .repositoryId( RepositoryId.from( "my-repo" ) )
            .principalsKeys( AuthenticationInfo.unAuthenticated().getPrincipals() )
            .build();

        final NodePath path = new NodePath( "/gjedde" );
        final NodeVersionKey key = NodeVersionKey.create()
            .nodeBlobKey( BlobKey.from( "sha256:node" ) )
            .indexConfigBlobKey( BlobKey.from( "sha256:index" ) )
            .accessControlBlobKey( BlobKey.from( "sha256:access" ) )
            .build();
        final NodeSegments segments = new NodeSegments( new PayloadSegment( "sha256:node", "node-bytes".getBytes() ),
                                                          new PayloadSegment( "sha256:index", "index-bytes".getBytes() ),
                                                          new PayloadSegment( "sha256:access", "access-bytes".getBytes() ) );

        // First store at the path establishes the cache entry for node "1".
        this.branchService.store( NodeBranchEntry.create()
                                       .nodeId( NodeId.from( "1" ) )
                                       .nodePath( path )
                                       .nodeVersionId( NodeVersionId.from( "v1" ) )
                                       .nodeVersionKey( key )
                                       .timestamp( Instant.now() )
                                       .build(), context );

        // A DIFFERENT node trying to save at the same path must be rejected, exactly like
        // the plain store() path already guarantees.
        final NodeBranchEntry conflicting = NodeBranchEntry.create()
            .nodeId( NodeId.from( "2" ) )
            .nodePath( path )
            .nodeVersionId( NodeVersionId.from( "v2" ) )
            .nodeVersionKey( key )
            .timestamp( Instant.now() )
            .build();
        final NodeVersion conflictingVersion = NodeVersion.create()
            .nodeId( NodeId.from( "2" ) )
            .nodeVersionId( NodeVersionId.from( "v2" ) )
            .nodeVersionKey( key )
            .binaryBlobKeys( BlobKeys.empty() )
            .nodePath( path )
            .timestamp( Instant.now() )
            .build();

        assertThrows( NodeAlreadyExistAtPathException.class,
                       () -> this.branchService.storeWithVersion( conflicting, conflictingVersion, segments, context ) );
    }
}
