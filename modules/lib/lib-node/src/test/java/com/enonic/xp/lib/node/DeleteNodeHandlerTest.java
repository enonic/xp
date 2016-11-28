package com.enonic.xp.lib.node;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;

public class DeleteNodeHandlerTest
    extends BaseNodeHandlerTest
{
    private void mockGetNode()
    {
        Mockito.when( this.nodeService.deleteById( Mockito.any() ) ).
            thenReturn( NodeIds.empty() );
        Mockito.when( this.nodeService.deleteByPath( Mockito.any() ) ).
            thenReturn( NodeIds.empty() );

        final NodeIds nodeIds = NodeIds.from( "nodeId", "aSubNodeId" );
        Mockito.when( this.nodeService.deleteById( NodeId.from( "nodeId" ) ) ).
            thenReturn( nodeIds );
        Mockito.when( this.nodeService.deleteByPath( NodePath.create( "/node2-path" ).build() ) ).
            thenReturn( nodeIds );
    }

    @Test
    public void testExample()
    {
        mockGetNode();

        Mockito.when( this.repositoryService.get( RepositoryId.from( "cms-repo" ) ) ).
            thenReturn( Repository.create().
                id( RepositoryId.from( "cms-repo" ) ).
                branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) ).
                build() );

        runScript( "/site/lib/xp/examples/node/delete.js" );
    }

}