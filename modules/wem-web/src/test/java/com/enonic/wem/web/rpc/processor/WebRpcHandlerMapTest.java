package com.enonic.wem.web.rpc.processor;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.web.rpc.WebRpcContext;
import com.enonic.wem.web.rpc.WebRpcException;
import com.enonic.wem.web.rpc.WebRpcHandler;

import static org.junit.Assert.*;

public class WebRpcHandlerMapTest
{
    private WebRpcHandlerMap handlerMap;

    private WebRpcHandler handler;

    @Before
    public void setUp()
    {
        this.handler = new WebRpcHandler( "myMethod" )
        {
            @Override
            public void handle( final WebRpcContext context )
                throws Exception
            {
            }
        };

        this.handlerMap = new WebRpcHandlerMap( this.handler );
    }

    @Test
    public void testFound()
        throws Exception
    {
        final WebRpcHandler result = this.handlerMap.getHandler( "myMethod" );
        assertNotNull( result );
        assertSame( this.handler, result );
    }

    @Test(expected = WebRpcException.class)
    public void testNotFound()
        throws Exception
    {
        this.handlerMap.getHandler( "otherMethod" );
    }
}
