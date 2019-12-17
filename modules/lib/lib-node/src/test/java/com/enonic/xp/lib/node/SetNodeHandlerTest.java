package com.enonic.xp.lib.node;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.node.SetNodeChildOrderParams;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SetNodeHandlerTest
    extends BaseNodeHandlerTest
{
    @Test
    public void testSetChildOrder()
    {
        final ArgumentCaptor<SetNodeChildOrderParams> argumentCaptor = ArgumentCaptor.forClass( SetNodeChildOrderParams.class );
        Mockito.when( nodeService.setChildOrder( Mockito.any() ) ).thenReturn( createNode() );
        runScript( "/lib/xp/examples/node/setChildOrder.js" );
        Mockito.verify( nodeService ).setChildOrder( argumentCaptor.capture() );

        assertEquals( "nodeId", argumentCaptor.getValue().getNodeId().toString() );
        assertEquals( "field DESC", argumentCaptor.getValue().getChildOrder().toString() );
    }
}
