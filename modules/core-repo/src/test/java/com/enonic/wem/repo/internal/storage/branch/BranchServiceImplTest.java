package com.enonic.wem.repo.internal.storage.branch;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.branch.StoreBranchDocument;
import com.enonic.wem.repo.internal.storage.GetByValuesRequest;
import com.enonic.wem.repo.internal.storage.StorageDao;
import com.enonic.wem.repo.internal.storage.StoreRequest;
import com.enonic.wem.repo.internal.storage.result.SearchResult;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.junit.Assert.*;

public class BranchServiceImplTest
{

    private BranchServiceImpl branchService;

    private StorageDao storageDao;

    private InternalContext context;

    @Before
    public void setup()
        throws Exception
    {
        this.storageDao = Mockito.mock( StorageDao.class );

        this.branchService = new BranchServiceImpl();
        this.branchService.setStorageDao( storageDao );

        context = InternalContext.create().
            branch( Branch.from( "myBranch" ) ).
            authInfo( AuthenticationInfo.unAuthenticated() ).
            repositoryId( RepositoryId.from( "myRepo" ) ).
            build();
    }

    @Test
    public void store_fetch_with_id_from_cache()
        throws Exception
    {
        Mockito.when( this.storageDao.store( Mockito.isNotNull( StoreRequest.class ) ) ).
            thenReturn( "myId" );

        this.branchService.store( StoreBranchDocument.create().
            node( Node.create().
                id( NodeId.from( "myId" ) ).
                parentPath( NodePath.ROOT ).
                name( "myNode" ).
                build() ).
            nodeVersionId( NodeVersionId.from( "versionId" ) ).
            build(), this.context );

        assertNotNull( this.branchService.get( NodeId.from( "myId" ), context ) );
    }

    @Test
    public void store_fetch_with_path_from_cache()
        throws Exception
    {
        Mockito.when( this.storageDao.store( Mockito.isNotNull( StoreRequest.class ) ) ).
            thenReturn( "myId" );

        this.branchService.store( StoreBranchDocument.create().
            node( Node.create().
                id( NodeId.from( "myId" ) ).
                parentPath( NodePath.ROOT ).
                name( "myNode" ).
                build() ).
            nodeVersionId( NodeVersionId.from( "versionId" ) ).
            build(), this.context );

        assertNotNull( this.branchService.get( NodePath.create( "/myNode" ).build(), context ) );
    }

    @Test
    public void store_update_fetch_from_cache()
        throws Exception
    {
        Mockito.when( this.storageDao.store( Mockito.isNotNull( StoreRequest.class ) ) ).
            thenReturn( "myId" );

        this.branchService.store( StoreBranchDocument.create().
            node( Node.create().
                id( NodeId.from( "myId" ) ).
                parentPath( NodePath.ROOT ).
                name( "myNode" ).
                build() ).
            nodeVersionId( NodeVersionId.from( "versionId" ) ).
            build(), this.context );

        assertNotNull( this.branchService.get( NodePath.create( "/myNode" ).build(), context ) );

        this.branchService.store( StoreBranchDocument.create().
            node( Node.create().
                id( NodeId.from( "myId" ) ).
                parentPath( NodePath.ROOT ).
                name( "myNode_edit" ).
                build() ).
            nodeVersionId( NodeVersionId.from( "versionId" ) ).
            build(), this.context );

        Mockito.when( this.storageDao.getByValues( Mockito.isA( GetByValuesRequest.class ) ) ).
            thenReturn( SearchResult.empty() );

        assertNull( this.branchService.get( NodePath.create( "/myNode" ).build(), context ) );
        assertNotNull( this.branchService.get( NodePath.create( "/myNode_edit" ).build(), context ) );
    }


}