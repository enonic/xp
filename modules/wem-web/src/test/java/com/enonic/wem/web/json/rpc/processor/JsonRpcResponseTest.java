package com.enonic.wem.web.json.rpc.processor;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.junit.Test;

import com.enonic.wem.web.json.rpc.JsonRpcError;

import static org.junit.Assert.*;

public class JsonRpcResponseTest
{
    @Test
    public void testBasic()
    {
        final JsonRpcResponse res = new JsonRpcResponse();

        assertNull( res.getId() );
        res.setId( "id" );
        assertEquals( "id", res.getId() );

        assertNull( res.getMethod() );
        res.setMethod( "method" );
        assertEquals( "method", res.getMethod() );

        final JsonRpcError error = JsonRpcError.internalError( "Message" );
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
        final JsonRpcRequest req = new JsonRpcRequest();
        req.setId( "id" );
        req.setMethod( "method" );

        final JsonRpcError error = JsonRpcError.internalError( "Message" );
        req.setError( error );

        final JsonRpcResponse res = JsonRpcResponse.from( req );
        assertEquals( "id", res.getId() );
        assertEquals( "method", res.getMethod() );
        assertSame( error, res.getError() );
    }
}
