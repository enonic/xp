package com.enonic.wem.web.rpc;

import org.junit.Test;

import static org.junit.Assert.*;

public class JsonRpcErrorTest
{
    @Test
    public void testMethodNotFound()
    {
        final JsonRpcError error = JsonRpcError.methodNotFound( "Message" );
        assertNotNull( error );
        assertEquals( -32601, error.getCode() );
        assertEquals( 404, error.getHttpStatus() );
        assertEquals( "Method not found: Message", error.getMessage() );
    }

    @Test
    public void testInternalError()
    {
        final JsonRpcError error = JsonRpcError.internalError( "Message" );
        assertNotNull( error );
        assertEquals( -32603, error.getCode() );
        assertEquals( 500, error.getHttpStatus() );
        assertEquals( "Internal Error: Message", error.getMessage() );
    }

    @Test
    public void testParseError()
    {
        final JsonRpcError error = JsonRpcError.parseError( "Message" );
        assertNotNull( error );
        assertEquals( -32700, error.getCode() );
        assertEquals( 500, error.getHttpStatus() );
        assertEquals( "Parse error: Message", error.getMessage() );
    }

    @Test
    public void testInvalidParams()
    {
        final JsonRpcError error = JsonRpcError.invalidParams( "Message" );
        assertNotNull( error );
        assertEquals( -32602, error.getCode() );
        assertEquals( 400, error.getHttpStatus() );
        assertEquals( "Invalid params: Message", error.getMessage() );
    }

    @Test
    public void testInvalidRequest()
    {
        final JsonRpcError error = JsonRpcError.invalidRequest( "Message" );
        assertNotNull( error );
        assertEquals( -32600, error.getCode() );
        assertEquals( 500, error.getHttpStatus() );
        assertEquals( "Invalid request: Message", error.getMessage() );
    }
}
