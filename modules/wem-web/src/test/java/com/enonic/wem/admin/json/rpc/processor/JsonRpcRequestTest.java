package com.enonic.wem.admin.json.rpc.processor;

import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;

import com.enonic.wem.admin.json.rpc.JsonRpcError;

import static org.junit.Assert.*;

public class JsonRpcRequestTest
{
    @Test
    public void testBasic()
    {
        final JsonRpcRequest req = new JsonRpcRequest();

        assertNull( req.getId() );
        req.setId( "id" );
        assertEquals( "id", req.getId() );

        assertNull( req.getMethod() );
        req.setMethod( "method" );
        assertEquals( "method", req.getMethod() );

        final JsonRpcError error = JsonRpcError.internalError( "Message" );
        assertNull( req.getError() );
        req.setError( error );
        assertSame( error, req.getError() );

        final ObjectNode params = JsonNodeFactory.instance.objectNode();
        assertNull( req.getParams() );
        req.setParams( params );
        assertEquals( params, req.getParams() );
    }
}
