package com.enonic.wem.web.rpc;

import org.junit.Test;

import static org.junit.Assert.*;

public class WebRpcExceptionTest
{
    @Test
    public void testBasic()
    {
        final WebRpcError error = WebRpcError.methodNotFound( "Message" );
        final WebRpcException ex = new WebRpcException( error );

        assertSame( error, ex.getError() );
        assertEquals( "Method not found: Message", ex.getMessage() );
    }
}
