package com.enonic.xp.lib.node;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;

public class GetNodeHandlerTest
    extends BaseNodeHandlerTest
{
    private void mockGetNode()
    {
        final Node node = createNode();
        Mockito.when( this.nodeService.getById( NodeId.from( "nodeId" ) ) ).
            thenReturn( node );
        Mockito.when( this.nodeService.getByPath( NodePath.create( "/node2-path" ).build() ) ).
            thenReturn( node );
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

        runScript( "/site/lib/xp/examples/node/get-1.js" );
    }

    @Test
    public void testExample2()
    {
        Mockito.when( this.nodeService.getById( Mockito.isA( NodeId.class ) ) ).
            thenReturn( createNode() );

        Mockito.when( this.repositoryService.get( RepositoryId.from( "com.enonic.cms.default" ) ) ).
            thenReturn( Repository.create().
                id( RepositoryId.from( "com.enonic.cms.default" ) ).
                branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) ).
                build() );

        runScript( "/site/lib/xp/examples/node/get-2.js" );
    }

}