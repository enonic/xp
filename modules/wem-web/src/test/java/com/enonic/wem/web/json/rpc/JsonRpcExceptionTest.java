package com.enonic.wem.web.json.rpc;

import org.junit.Test;

import static org.junit.Assert.*;

public class JsonRpcExceptionTest
{
    @Test
    public void testBasic()
    {
        final JsonRpcError error = JsonRpcError.methodNotFound( "Message" );
        final JsonRpcException ex = new JsonRpcException( error );

        assertSame( error, ex.getError() );
        assertEquals( "Method not found: Message", ex.getMessage() );
    }
}
