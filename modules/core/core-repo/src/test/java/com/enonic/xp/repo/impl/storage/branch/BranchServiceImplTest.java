package com.enonic.xp.repo.impl.storage.branch;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.InternalContext;
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

import static org.junit.Assert.*;

public class BranchServiceImplTest
{

    private BranchServiceImpl branchService;

    private StorageDao storageDao;

    private SearchDao searchDao;

    private InternalContext context;

    @Before
    public void setup()
        throws Exception
    {
        this.storageDao = Mockito.mock( StorageDao.class );
        this.searchDao = Mockito.mock( SearchDao.class );

        this.branchService = new BranchServiceImpl();
        this.branchService.setStorageDao( storageDao );
        this.branchService.setSearchDao( searchDao );

        context = InternalContext.create().
            branch( Branch.from( "myBranch" ) ).
            authInfo( AuthenticationInfo.unAuthenticated() ).
            repositoryId( RepositoryId.from( "my-repo" ) ).
            build();
    }


    @Test
    public void path_fetched_from_cache_after_stored()
        throws Exception
    {
        final InternalContext context = InternalContext.create().
            branch( Branch.from( "myBranch" ) ).
            repositoryId( RepositoryId.from( "my-repo" ) ).
            build();

        final NodePath path = NodePath.create( NodePath.ROOT, "fisk" ).build();

        Mockito.when( this.storageDao.store( Mockito.isA( StoreRequest.class ) ) ).
            thenReturn( "123_myBranch" );

        this.branchService.store( NodeBranchEntry.create().
            nodeId( NodeId.from( "123" ) ).
            nodePath( path ).
            nodeState( NodeState.DEFAULT ).
            nodeVersionId( NodeVersionId.from( "nodeVersionId" ) ).
            timestamp( Instant.now() ).
            build(), context );

        Mockito.when( this.storageDao.getById( Mockito.isA( GetByIdRequest.class ) ) ).
            thenReturn( GetResult.create().
                id( "123_myBranch" ).
                resultFieldValues( ReturnValues.create().
                    add( BranchIndexPath.PATH.getPath(), "/fisk" ).
                    add( BranchIndexPath.STATE.getPath(), "default" ).
                    add( BranchIndexPath.VERSION_ID.getPath(), "nodeVersionId" ).
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