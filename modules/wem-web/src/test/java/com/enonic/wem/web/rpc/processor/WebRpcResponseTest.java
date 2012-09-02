package com.enonic.wem.web.rpc.processor;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.junit.Test;

import com.enonic.wem.web.rpc.WebRpcError;

import static org.junit.Assert.*;

public class WebRpcResponseTest
{
    @Test
    public void testBasic()
    {
        final WebRpcResponse res = new WebRpcResponse();

        assertNull( res.getId() );
        res.setId( "id" );
        assertEquals( "id", res.getId() );

        assertNull( res.getMethod() );
        res.setMethod( "method" );
        assertEquals( "method", res.getMethod() );

        final WebRpcError error = WebRpcError.internalError( "Message" );
        assertNull( res.getError() );
        res.setError( error );
        assertSame( error, res.getError() );

        final JsonNode result = JsonNodeFactory.instance.objectNode();
        assertNull( res.getResult() );
        res.setResult( result );
        assertEquals( result, res.getResult() );
    }

    @Test
    public void testCreateFrom()
    {
        final WebRpcRequest req = new WebRpcRequest();
        req.setId( "id" );
        req.setMethod( "method" );

        final WebRpcError error = WebRpcError.internalError( "Message" );
        req.setError( error );

        final WebRpcResponse res = WebRpcResponse.from( req );
        assertEquals( "id", res.getId() );
        assertEquals( "method", res.getMethod() );
        assertSame( error, res.getError() );
    }
}
