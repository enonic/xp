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
import com.enonic.xp.repo.impl.ReturnValues;
import com.enonic.xp.repo.impl.branch.storage.BranchIndexPath;
import com.enonic.xp.repo.impl.branch.storage.BranchServiceImpl;
import com.enonic.xp.repo.impl.search.SearchDao;
import com.enonic.xp.repo.impl.search.SearchRequest;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.storage.GetByIdRequest;
import com.enonic.xp.repo.impl.storage.GetResult;
import com.enonic.xp.repo.impl.storage.StorageDao;
import com.enonic.xp.repo.impl.storage.StoreRequest;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class BranchServiceImplTest
{
    private BranchServiceImpl branchService;

    private StorageDao storageDao;

    private SearchDao searchDao;

    @BeforeEach
    void setup()
    {
        this.storageDao = Mockito.mock( StorageDao.class );
        this.searchDao = Mockito.mock( SearchDao.class );

        this.branchService = new BranchServiceImpl( storageDao, searchDao );
    }

    @Test
    void path_fetched_from_cache_after_stored()
    {
        final InternalContext context = InternalContext.create().
            branch( Branch.from( "myBranch" ) ).
            repositoryId( RepositoryId.from( "my-repo" ) ).
            principalsKeys( AuthenticationInfo.unAuthenticated().getPrincipals() ).
            build();

        final NodePath path = new NodePath( "/fisk" );

        Mockito.when( this.storageDao.store( Mockito.isA( StoreRequest.class ) ) ).
            thenReturn( "123_myBranch" );

        this.branchService.store( NodeBranchEntry.create().
            nodeId( NodeId.from( "123" ) ).
            nodePath( path ).
            nodeVersionId( NodeVersionId.from( "nodeVersionId" ) ).
            nodeVersionKey( NodeVersionKey.create()
                                .nodeBlobKey( BlobKey.from( "nodeBlobKey" ) )
                                .indexConfigBlobKey( BlobKey.from( "indexConfigBlobKey" ) )
                                .accessControlBlobKey( BlobKey.from( "accessControlBlobKey" ) )
                                .build() ).
            timestamp( Instant.now() ).
            build(), context );

        Mockito.when( this.storageDao.getById( Mockito.isA( GetByIdRequest.class ) ) ).
            thenReturn( GetResult.create().
                id( "123_myBranch" ).
                resultFieldValues( ReturnValues.create().
                    add( BranchIndexPath.PATH.getPath(), "/fisk" ).
                    add( BranchIndexPath.STATE.getPath(), "default" ).
                    add( BranchIndexPath.VERSION_ID.getPath(), "nodeVersionId" ).
                    add( BranchIndexPath.NODE_BLOB_KEY.getPath(), "nodeBlobKey" ).
                    add( BranchIndexPath.INDEX_CONFIG_BLOB_KEY.getPath(), "indexConfigBlobKey" ).
                    add( BranchIndexPath.ACCESS_CONTROL_BLOB_KEY.getPath(), "accessControlBlobKey" ).
                    add( BranchIndexPath.NODE_ID.getPath(), "123" ).
                    add( BranchIndexPath.TIMESTAMP.getPath(), Instant.now().toString() ).
                    build() ).
                build() );

        Mockito.when( this.searchDao.search( Mockito.isA( SearchRequest.class ) ) ).
            thenReturn( SearchResult.create().build() );

        final NodeBranchEntry fetchEntry = this.branchService.get( path, context );

        assertNotNull( fetchEntry );
    }
}
