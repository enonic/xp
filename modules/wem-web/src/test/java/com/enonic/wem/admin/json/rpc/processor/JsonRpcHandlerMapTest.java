package com.enonic.wem.admin.json.rpc.processor;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

import com.enonic.wem.admin.json.rpc.JsonRpcContext;
import com.enonic.wem.admin.json.rpc.JsonRpcException;
import com.enonic.wem.admin.json.rpc.JsonRpcHandler;

import static org.junit.Assert.*;

public class JsonRpcHandlerMapTest
{
    private JsonRpcHandlerMap handlerMap;

    private JsonRpcHandler handler;

    @Before
    public void setUp()
    {
        this.handler = new JsonRpcHandler( "myMethod" )
        {
            @Override
            public void handle( final JsonRpcContext context )
                throws Exception
            {
            }
        };

        this.handlerMap = new JsonRpcHandlerMap( Sets.newHashSet( this.handler ) );
    }

    @Test
    public void testFound()
        throws Exception
    {
        final JsonRpcHandler result = this.handlerMap.getHandler( "myMethod" );
        assertNotNull( result );
        assertSame( this.handler, result );
    }

    @Test(expected = JsonRpcException.class)
    public void testNotFound()
        throws Exception
    {
        this.handlerMap.getHandler( "otherMethod" );
    }
}
