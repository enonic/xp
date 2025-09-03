package com.enonic.xp.lib.node;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.node.SortNodeParams;
import com.enonic.xp.node.SortNodeResult;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SortNodeHandlerTest
    extends BaseNodeHandlerTest
{
    @Test
    public void testSetChildOrder()
    {
        final ArgumentCaptor<SortNodeParams> argumentCaptor = ArgumentCaptor.forClass( SortNodeParams.class );
        Mockito.when( nodeService.sort( Mockito.any() ) ).thenReturn( SortNodeResult.create().node( createNode() ).build() );
        runScript( "/lib/xp/examples/node/sort.js" );
        Mockito.verify( nodeService ).sort( argumentCaptor.capture() );

        assertEquals( "nodeId", argumentCaptor.getValue().getNodeId().toString() );
        assertEquals( "field DESC", argumentCaptor.getValue().getChildOrder().toString() );
    }
}
