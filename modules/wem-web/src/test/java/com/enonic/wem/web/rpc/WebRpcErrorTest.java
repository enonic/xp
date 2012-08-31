package com.enonic.wem.web.rpc;

import org.junit.Test;

import static org.junit.Assert.*;

public class WebRpcErrorTest
{
    @Test
    public void testMethodNotFound()
    {
        final WebRpcError error = WebRpcError.methodNotFound( "Message" );
        assertNotNull( error );
        assertEquals( -32601, error.getCode() );
        assertEquals( 404, error.getHttpStatus() );
        assertEquals( "Method not found: Message", error.getMessage() );
    }

    @Test
    public void testInternalError()
    {
        final WebRpcError error = WebRpcError.internalError( "Message" );
        assertNotNull( error );
        assertEquals( -32603, error.getCode() );
        assertEquals( 500, error.getHttpStatus() );
        assertEquals( "Internal Error: Message", error.getMessage() );
    }

    @Test
    public void testParseError()
    {
        final WebRpcError error = WebRpcError.parseError( "Message" );
        assertNotNull( error );
        assertEquals( -32700, error.getCode() );
        assertEquals( 500, error.getHttpStatus() );
        assertEquals( "Parse error: Message", error.getMessage() );
    }

    @Test
    public void testInvalidParams()
    {
        final WebRpcError error = WebRpcError.invalidParams( "Message" );
        assertNotNull( error );
        assertEquals( -32602, error.getCode() );
        assertEquals( 400, error.getHttpStatus() );
        assertEquals( "Invalid params: Message", error.getMessage() );
    }

    @Test
    public void testInvalidRequest()
    {
        final WebRpcError error = WebRpcError.invalidRequest( "Message" );
        assertNotNull( error );
        assertEquals( -32600, error.getCode() );
        assertEquals( 500, error.getHttpStatus() );
        assertEquals( "Invalid request: Message", error.getMessage() );
    }
}
