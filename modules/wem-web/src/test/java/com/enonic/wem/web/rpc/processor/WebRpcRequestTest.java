package com.enonic.wem.web.rpc.processor;

import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;

import com.enonic.wem.web.rpc.WebRpcError;

import static org.junit.Assert.*;

public class WebRpcRequestTest
{
    @Test
    public void testBasic()
    {
        final WebRpcRequest req = new WebRpcRequest();

        assertNull( req.getId() );
        req.setId( "id" );
        assertEquals( "id", req.getId() );

        assertNull( req.getAction() );
        req.setAction( "action" );
        assertEquals( "action", req.getAction() );

        assertNull( req.getMethod() );
        req.setMethod( "method" );
        assertEquals( "method", req.getMethod() );

        final WebRpcError error = WebRpcError.internalError( "Message" );
        assertNull( req.getError() );
        req.setError( error );
        assertSame( error, req.getError() );

        final ObjectNode params = JsonNodeFactory.instance.objectNode();
        assertNull( req.getParams() );
        req.setParams( params );
        assertEquals( params, req.getParams() );
    }
}
