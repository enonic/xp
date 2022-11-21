package com.enonic.xp.lib.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.node.MoveNodeParams;
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

    @BeforeEach
    public void setUp()
    {
        parentPath = NodePath.ROOT;
        name = "my-name";
    }

    private void mockGetNode()
    {
        Mockito.when( this.nodeService.getById( NodeId.from( "nodeId" ) ) ).thenReturn( createNode() );
        Mockito.when( this.nodeService.getByPath( NodePath.create( "/my-name" ).build() ) ).thenReturn( createNode() );

        Mockito.when( this.nodeService.rename( Mockito.any() ) ).thenAnswer( invocation -> {
            final RenameNodeParams renameNodeParams = invocation.getArgument( 0 );
            name = renameNodeParams.getNewNodeName().toString();
            return createNode();
        } );
        Mockito.when( this.nodeService.move( Mockito.any() ) ).thenAnswer( invocation -> {
            final MoveNodeParams moveNodeParams = invocation.getArgument( 0 );
            parentPath = moveNodeParams.getParentNodePath().trimTrailingDivider();
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

        Mockito.when( this.repositoryService.get( RepositoryId.from( "com.enonic.cms.default" ) ) ).
            thenReturn( Repository.create().
                id( RepositoryId.from( "com.enonic.cms.default" ) ).
                branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) ).
                build() );

        runScript( "/lib/xp/examples/node/move-1.js" );
    }

    @Test
    public void testExample2()
    {
        mockGetNode();

        Mockito.when( this.repositoryService.get( RepositoryId.from( "com.enonic.cms.default" ) ) ).
            thenReturn( Repository.create().
                id( RepositoryId.from( "com.enonic.cms.default" ) ).
                branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) ).
                build() );

        runScript( "/lib/xp/examples/node/move-2.js" );
    }

    @Test
    public void testExample3()
    {
        mockGetNode();

        Mockito.when( this.repositoryService.get( RepositoryId.from( "com.enonic.cms.default" ) ) ).
            thenReturn( Repository.create().
                id( RepositoryId.from( "com.enonic.cms.default" ) ).
                branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) ).
                build() );

        runScript( "/lib/xp/examples/node/move-3.js" );
    }

}
