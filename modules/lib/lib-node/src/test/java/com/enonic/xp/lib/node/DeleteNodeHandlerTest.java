package com.enonic.xp.lib.node;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.node.DeleteNodeResult;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

class DeleteNodeHandlerTest
    extends BaseNodeHandlerTest
{
    private void mockGetNode()
    {
        when( this.nodeService.delete( any() ) ).thenReturn( DeleteNodeResult.create().nodeIds( NodeIds.empty() ).build() );

        final DeleteNodeResult result = DeleteNodeResult.create()
            .nodeIds( NodeIds.from(NodeId.from( "nodeId" ), NodeId.from( "aSubNodeId" ) ) )
            .build();
        doReturn( result ).when( this.nodeService )
            .delete( ArgumentMatchers.argThat( argument -> NodeId.from( "nodeId" ).equals( argument.getNodeId() ) ) );
        doReturn( result ).when( this.nodeService )
            .delete( ArgumentMatchers.argThat( argument -> new NodePath( "/node2-path" ).equals( argument.getNodePath() ) ) );
    }

    @Test
    void testExample()
    {
        mockGetNode();

        when( this.repositoryService.get( RepositoryId.from( "com.enonic.cms.default" ) ) ).
            thenReturn( Repository.create().
                id( RepositoryId.from( "com.enonic.cms.default" ) ).
                branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) ).
                build() );

        runScript( "/lib/xp/examples/node/delete.js" );
    }

}
