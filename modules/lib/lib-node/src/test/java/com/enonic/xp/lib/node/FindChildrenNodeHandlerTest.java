package com.enonic.xp.lib.node;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.NodeIds;

class FindChildrenNodeHandlerTest
    extends BaseNodeHandlerTest
{
    @Test
    void testExample()
    {
        Mockito.when( this.nodeService.findByParent( Mockito.isA( FindNodesByParentParams.class ) ) ).
            thenReturn( FindNodesByParentResult.create().
                totalHits( 12902 ).
                nodeIds( NodeIds.from( "b186d24f-ac38-42ca-a6db-1c1bda6c6c26", "350ba4a6-589c-498b-8af0-f183850e1120" ) ).
                build() );

        runScript( "/lib/xp/examples/node/findChildren.js" );
    }


}
