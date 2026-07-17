package com.enonic.xp.repo.impl.storage.branch;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.repo.impl.branch.storage.BranchServiceImpl;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.storage.spi.BranchEntryRecord;
import com.enonic.xp.storage.spi.NodeStore;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}
