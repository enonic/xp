package com.enonic.xp.lib.node;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.util.BinaryReference;

class GetBinaryStreamHandlerTest
    extends BaseNodeHandlerTest
{
    private void mockGetBinary()
    {
        final Node node = createNode();
        Mockito.when( this.nodeService.getByPath( new NodePath( "/myNode" ) ) ).
            thenReturn( node );

        Mockito.when( this.nodeService.getBinary( Mockito.isA( NodeId.class ), Mockito.isA( BinaryReference.class ) ) ).
            thenReturn( ByteSource.wrap( "this is a binary file".getBytes() ) );
    }

    @Test
    void testExample()
    {
        mockGetBinary();
        runScript( "/lib/xp/examples/node/getBinary.js" );
    }
}
