package com.enonic.xp.lib.node;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersionId;

import static org.junit.Assert.*;

public class SetActiveVersionHandlerTest
    extends BaseNodeHandlerTest
{
    @Test
    public void testSetActiveVersionHandler()
    {
        final NodeVersionId nodeVersionId = NodeVersionId.from( "90398ddd1" );
        final ArgumentCaptor<NodeId> nodeIdCaptor = ArgumentCaptor.forClass( NodeId.class );
        final ArgumentCaptor<NodeVersionId> nodeVersionIdCaptor = ArgumentCaptor.forClass( NodeVersionId.class );
        Mockito.when( nodeService.setActiveVersion( Mockito.any(), Mockito.any() ) ).thenReturn( nodeVersionId );
        runScript( "/lib/xp/examples/node/setActiveVersion.js" );
        Mockito.verify( nodeService ).setActiveVersion( nodeIdCaptor.capture(), nodeVersionIdCaptor.capture() );

        assertEquals( "nodeId", nodeIdCaptor.getValue().toString() );
        assertEquals( nodeVersionId, nodeVersionIdCaptor.getValue() );
    }
}
