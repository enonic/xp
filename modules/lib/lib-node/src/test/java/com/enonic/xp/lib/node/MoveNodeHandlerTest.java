package com.enonic.xp.lib.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.node.MoveNodeParams;
import com.enonic.xp.node.MoveNodeResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;

class MoveNodeHandlerTest
    extends BaseNodeHandlerTest
{
    private NodePath parentPath;

    private NodeName name;

    @BeforeEach
    void setUp()
    {
        parentPath = NodePath.ROOT;
        name = NodeName.from( "my-name" );
    }

    private void mockGetNode()
    {
        Mockito.when( this.nodeService.getById( NodeId.from( "nodeId" ) ) ).thenReturn( createNode() );
        Mockito.when( this.nodeService.getByPath( new NodePath( "/my-name" ) ) ).thenReturn( createNode() );

        Mockito.when( this.nodeService.move( Mockito.any() ) ).thenAnswer( invocation -> {
            final MoveNodeParams moveNodeParams = invocation.getArgument( 0 );
            if ( moveNodeParams.getNewParentPath() != null )
            {
                parentPath = moveNodeParams.getNewParentPath();
            }
            if ( moveNodeParams.getNewNodeName() != null )
            {
                name = moveNodeParams.getNewNodeName();
            }

            return MoveNodeResult.create()
                .addMovedNode( MoveNodeResult.MovedNode.create().previousPath( new NodePath( "/my-name" ) ).node( createNode() ).build() )
                .build();
        } );
    }

    @Override
    protected Node createNode()
    {
        return super.createNode( parentPath, name );
    }

    @Test
    void testExample1()
    {
        mockGetNode();

        Mockito.when( this.repositoryService.get( RepositoryId.from( "com.enonic.cms.default" ) ) )
            .thenReturn( Repository.create()
                             .id( RepositoryId.from( "com.enonic.cms.default" ) )
                             .branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) )
                             .build() );

        runScript( "/lib/xp/examples/node/move-1.js" );
    }

    @Test
    void testExample2()
    {
        mockGetNode();

        Mockito.when( this.repositoryService.get( RepositoryId.from( "com.enonic.cms.default" ) ) )
            .thenReturn( Repository.create()
                             .id( RepositoryId.from( "com.enonic.cms.default" ) )
                             .branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) )
                             .build() );

        runScript( "/lib/xp/examples/node/move-2.js" );
    }

    @Test
    void testExample3()
    {
        mockGetNode();

        Mockito.when( this.repositoryService.get( RepositoryId.from( "com.enonic.cms.default" ) ) )
            .thenReturn( Repository.create()
                             .id( RepositoryId.from( "com.enonic.cms.default" ) )
                             .branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) )
                             .build() );

        runScript( "/lib/xp/examples/node/move-3.js" );
    }

}
