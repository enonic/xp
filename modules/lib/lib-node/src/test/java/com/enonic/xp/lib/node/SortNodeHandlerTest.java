package com.enonic.xp.lib.node;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.SortNodeParams;
import com.enonic.xp.node.SortNodeResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

class SortNodeHandlerTest
    extends BaseNodeHandlerTest
{
    @Test
    void testSort()
    {
        final ArgumentCaptor<SortNodeParams> argumentCaptor = ArgumentCaptor.forClass( SortNodeParams.class );
        final Node node = Node.create( createNode() ).childOrder( ChildOrder.from( "field DESC" ) ).build();

        Mockito.when( nodeService.sort( any() ) ).thenReturn( SortNodeResult.create().node( node ).build() );
        runScript( "/lib/xp/examples/node/sort.js" );
        Mockito.verify( nodeService ).sort( argumentCaptor.capture() );

        assertEquals( "nodeId", argumentCaptor.getValue().getNodeId().toString() );
        assertEquals( "field DESC", argumentCaptor.getValue().getChildOrder().toString() );
    }
}
