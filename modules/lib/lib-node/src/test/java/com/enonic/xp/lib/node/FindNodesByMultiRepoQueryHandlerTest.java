package com.enonic.xp.lib.node;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.FindNodesByMultiRepoQueryResult;
import com.enonic.xp.node.MultiRepoNodeHit;
import com.enonic.xp.node.MultiRepoNodeQuery;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.resource.ResourceProblemException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FindNodesByMultiRepoQueryHandlerTest
    extends BaseNodeHandlerTest
{
    @Test
    void testExample()
    {
        Mockito.when( this.nodeService.findByQuery( Mockito.isA( MultiRepoNodeQuery.class ) ) ).
            thenReturn( FindNodesByMultiRepoQueryResult.create().
                totalHits( 12902 ).
                addNodeHit( MultiRepoNodeHit.create().
                    branch( Branch.from( "master" ) ).
                    repositoryId( RepositoryId.from( "my-repo" ) ).
                    nodeId( NodeId.from( "b186d24f-ac38-42ca-a6db-1c1bda6c6c26" ) ).
                    score( 1.23f ).
                    build() ).
                addNodeHit( MultiRepoNodeHit.create().
                    branch( Branch.from( "draft" ) ).
                    repositoryId( RepositoryId.from( "com.enonic.cms.default" ) ).
                    nodeId( NodeId.from( "350ba4a6-589c-498b-8af0-f183850e1120" ) ).
                    score( 1.40f ).
                    build() ).
                build() );

        runScript( "/lib/xp/examples/node/multiRepoQuery.js" );
    }

    @Test
    void parentByPath()
    {
        Mockito.when( this.nodeService.findByQuery( Mockito.isA( MultiRepoNodeQuery.class ) ) )
            .thenReturn( FindNodesByMultiRepoQueryResult.create().totalHits( 2 ).build() );

        runFunction( "/test/FindNodesByMultiRepoQueryHandlerTest_parent.js", "parentByPath" );

        final ArgumentCaptor<MultiRepoNodeQuery> captor = ArgumentCaptor.forClass( MultiRepoNodeQuery.class );
        Mockito.verify( this.nodeService ).findByQuery( captor.capture() );

        final NodeQuery query = captor.getValue().getNodeQuery();
        assertEquals( new NodePath( "/parent" ), query.getParent() );
        assertTrue( query.isRecursive() );
    }

    @Test
    void parentById()
    {
        // one id cannot name a node across several repositories, so a multi-repo query takes a path parent only
        final ResourceProblemException e = assertThrows( ResourceProblemException.class, () -> runFunction(
            "/test/FindNodesByMultiRepoQueryHandlerTest_parent.js", "parentById" ) );
        assertEquals( "parent must be a path in a multi-repo query", e.getMessage() );

        Mockito.verify( this.nodeService, Mockito.never() ).findByQuery( Mockito.isA( MultiRepoNodeQuery.class ) );
    }
}
