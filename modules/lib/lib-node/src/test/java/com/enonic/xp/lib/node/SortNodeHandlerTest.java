package com.enonic.xp.lib.node;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.ReorderChildNodeParams;
import com.enonic.xp.node.SortNodeParams;
import com.enonic.xp.node.SortNodeResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;

class SortNodeHandlerTest
    extends BaseNodeHandlerTest
{
    @Test
    void testExample()
    {
        final ArgumentCaptor<SortNodeParams> argumentCaptor = ArgumentCaptor.forClass( SortNodeParams.class );
        final Node node = Node.create( createNode() ).childOrder( ChildOrder.from( "field DESC" ) ).build();

        Mockito.when( nodeService.sort( any() ) ).thenReturn( SortNodeResult.create().node( node ).build() );
        runScript( "/lib/xp/examples/node/sort.js" );
        Mockito.verify( nodeService, Mockito.times( 2 ) ).sort( argumentCaptor.capture() );

        final SortNodeParams byExpression = argumentCaptor.getAllValues().get( 0 );
        assertEquals( "nodeid", byExpression.getNodeId().toString() );
        assertEquals( "field DESC", byExpression.getChildOrder().toString() );
        assertEquals( List.of(), byExpression.getReorderChildNodes() );

        final SortNodeParams manual = argumentCaptor.getAllValues().get( 1 );
        assertEquals( ChildOrder.orderKeyOrder(), manual.getChildOrder() );
        assertEquals( 1, manual.getReorderChildNodes().size() );
        final ReorderChildNodeParams entry = manual.getReorderChildNodes().get( 0 );
        assertEquals( "child-node-id", entry.getNodeId().toString() );
        assertEquals( "3ala4x8dnbc.above-sibling-id", entry.getAfterOrderKey() );
        assertEquals( "3ala4xa1kfj.below-sibling-id", entry.getBeforeOrderKey() );
    }

    @Test
    void reorderWithoutChildOrder()
    {
        final ArgumentCaptor<SortNodeParams> argumentCaptor = ArgumentCaptor.forClass( SortNodeParams.class );
        Mockito.when( nodeService.sort( any() ) ).thenReturn( SortNodeResult.create().node( createNode() ).build() );

        runFunction( "/test/SortNodeHandlerTest.js", "reorderWithoutChildOrder" );

        Mockito.verify( nodeService ).sort( argumentCaptor.capture() );
        final SortNodeParams params = argumentCaptor.getValue();
        assertEquals( ChildOrder.orderKeyOrder(), params.getChildOrder(), "reorder alone flips to manual ordering" );

        final List<ReorderChildNodeParams> reorder = params.getReorderChildNodes();
        assertEquals( 2, reorder.size() );
        assertEquals( "child-1", reorder.get( 0 ).getNodeId().toString() );
        assertEquals( "3a00000zzzz.above", reorder.get( 0 ).getAfterOrderKey() );
        assertEquals( "3a00001zzzz.below", reorder.get( 0 ).getBeforeOrderKey() );
        assertEquals( "child-2", reorder.get( 1 ).getNodeId().toString() );
        assertNull( reorder.get( 1 ).getAfterOrderKey() );
        assertNull( reorder.get( 1 ).getBeforeOrderKey() );
    }

    @Test
    void missingParams()
    {
        runFunction( "/test/SortNodeHandlerTest.js", "missingParams" );

        Mockito.verify( nodeService, Mockito.never() ).sort( any() );
    }
}
