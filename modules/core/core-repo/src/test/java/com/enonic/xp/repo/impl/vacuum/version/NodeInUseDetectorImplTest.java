package com.enonic.xp.repo.impl.vacuum.version;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.BranchInfo;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repository.Repositories;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;

import static org.junit.Assert.*;

public class NodeInUseDetectorImplTest
{
    private NodeService nodeService;

    private RepositoryService repositoryService;

    @Before
    public void setUp()
        throws Exception
    {
        this.nodeService = Mockito.mock( NodeService.class );
        this.repositoryService = Mockito.mock( RepositoryService.class );
    }

    @Test
    public void not_in_use()
        throws Exception
    {
        final NodeInUseDetectorImpl detector = new NodeInUseDetectorImpl();
        detector.setNodeService( this.nodeService );
        detector.setRepositoryService( this.repositoryService );

        Mockito.when( this.repositoryService.list() ).thenReturn( Repositories.from( Repository.create().
            branchInfos( BranchInfo.from( "master" ) ).
            id( RepositoryId.from( "my-repo" ) ).
            build() ) );

        Mockito.when( this.nodeService.getById( NodeId.from( "a" ) ) ).
            thenThrow( new NodeNotFoundException( "not found" ) );

        assertFalse( detector.execute( NodeId.from( "a" ) ) );
    }

    @Test
    public void is_in_use()
        throws Exception
    {
        final NodeInUseDetectorImpl detector = new NodeInUseDetectorImpl();
        detector.setNodeService( this.nodeService );
        detector.setRepositoryService( this.repositoryService );

        Mockito.when( this.repositoryService.list() ).thenReturn( Repositories.from( Repository.create().
            branchInfos( BranchInfo.from( "master" ) ).
            id( RepositoryId.from( "my-repo" ) ).
            build() ) );

        final NodeId nodeId = NodeId.from( "a" );
        Mockito.when( this.nodeService.getById( nodeId ) ).
            thenReturn( Node.create( nodeId ).
                parentPath( NodePath.ROOT ).
                name( "myNode" ).
                build() );

        assertTrue( detector.execute( nodeId ) );
    }
}