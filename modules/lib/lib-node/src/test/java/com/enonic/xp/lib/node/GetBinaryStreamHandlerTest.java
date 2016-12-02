package com.enonic.xp.lib.node;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.util.BinaryReference;

public class GetBinaryStreamHandlerTest
    extends BaseNodeHandlerTest
{
    private void mockGetBinary()
    {
        final Node node = createNode();
        Mockito.when( this.nodeService.getByPath( NodePath.create( "/myNode" ).build() ) ).
            thenReturn( node );

        Mockito.when( this.nodeService.getBinary( Mockito.isA( NodeId.class ), Mockito.isA( BinaryReference.class ) ) ).
            thenReturn( ByteSource.wrap( "this is a binary file".getBytes() ) );
    }

    @Test
    public void testExample()
    {
        mockGetBinary();
        runScript( "/site/lib/xp/examples/node/getBinary.js" );
    }
}
