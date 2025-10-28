package com.enonic.xp.lib.node;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.FindNodesByMultiRepoQueryResult;
import com.enonic.xp.node.MultiRepoNodeHit;
import com.enonic.xp.node.MultiRepoNodeQuery;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repository.RepositoryId;

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


}
