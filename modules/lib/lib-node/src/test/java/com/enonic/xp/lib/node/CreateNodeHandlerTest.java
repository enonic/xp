package com.enonic.xp.lib.node;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;

public class CreateNodeHandlerTest
    extends BaseNodeHandlerTest
{
    private void mockCreateNode()
    {
        final Node node = createNode();
        Mockito.when( this.nodeService.create( Mockito.isA( CreateNodeParams.class ) ) ).
            thenReturn( node );
    }

    @Test
    public void testExample()
    {
        mockCreateNode();

        Mockito.when( this.repositoryService.get( RepositoryId.from( "cms-repo" ) ) ).
            thenReturn( Repository.create().
                id( RepositoryId.from( "cms-repo" ) ).
                branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) ).
                build() );

        runScript( "/site/lib/xp/examples/node/create.js" );
    }

}