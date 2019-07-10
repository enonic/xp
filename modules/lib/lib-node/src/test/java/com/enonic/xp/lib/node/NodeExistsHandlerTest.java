package com.enonic.xp.lib.node;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;

public class NodeExistsHandlerTest
    extends BaseNodeHandlerTest
{
    @Test
    public void testExistsByPath()
    {
        Mockito.when( this.nodeService.nodeExists( NodePath.create( "/my-name" ).build() ) ).thenReturn( true );

        runScript( "/lib/xp/examples/node/exists-1.js" );
    }

    @Test
    public void testExistsById()
    {
        Mockito.when( this.nodeService.nodeExists( NodeId.from( "123" ) ) ).thenReturn( true );

        runScript( "/lib/xp/examples/node/exists-2.js" );
    }

}
