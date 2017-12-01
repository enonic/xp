package com.enonic.xp.lib.node;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RenameNodeParams;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;

public class MoveNodeHandlerTest
    extends BaseNodeHandlerTest
{
    private NodePath parentPath;

    private String name;

    @Before
    public void setUp()
    {
        parentPath = NodePath.ROOT;
        name = "my-name";
    }

    private void mockGetNode()
    {
        Mockito.when( this.nodeService.getById( NodeId.from( "nodeId" ) ) ).
            thenReturn( createNode() );
        Mockito.when( this.nodeService.getByPath( NodePath.create( "/my-name" ).build() ) ).
            thenReturn( createNode() );

        Mockito.when( this.nodeService.rename( Mockito.any() ) ).
            thenAnswer( invocation -> {
                final RenameNodeParams renameNodeParams = (RenameNodeParams) invocation.getArguments()[0];
                name = renameNodeParams.getNewNodeName().toString();
                return createNode();
            } );
        Mockito.when( this.nodeService.move( Mockito.any( NodeId.class ), Mockito.any(), Mockito.any() ) ).
            thenAnswer( invocation -> {
                parentPath = ( (NodePath) invocation.getArguments()[1] ).trimTrailingDivider();
                return createNode();
            } );
    }

    @Override
    protected Node createNode()
    {
        return super.createNode( parentPath, name );
    }

    @Test
    public void testExample1()
    {
        mockGetNode();

        Mockito.when( this.repositoryService.get( RepositoryId.from( "cms-repo" ) ) ).
            thenReturn( Repository.create().
                id( RepositoryId.from( "cms-repo" ) ).
                branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) ).
                build() );

        runScript( "/site/lib/xp/examples/node/move-1.js" );
    }

    @Test
    public void testExample2()
    {
        mockGetNode();

        Mockito.when( this.repositoryService.get( RepositoryId.from( "cms-repo" ) ) ).
            thenReturn( Repository.create().
                id( RepositoryId.from( "cms-repo" ) ).
                branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) ).
                build() );

        runScript( "/site/lib/xp/examples/node/move-2.js" );
    }

    @Test
    public void testExample3()
    {
        mockGetNode();

        Mockito.when( this.repositoryService.get( RepositoryId.from( "cms-repo" ) ) ).
            thenReturn( Repository.create().
                id( RepositoryId.from( "cms-repo" ) ).
                branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) ).
                build() );

        runScript( "/site/lib/xp/examples/node/move-3.js" );
    }

}
