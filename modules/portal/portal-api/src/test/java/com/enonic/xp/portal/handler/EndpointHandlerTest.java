package com.enonic.xp.portal.handler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandlerChain;

public class EndpointHandlerTest
{
    private EndpointHandler endpointHandler;

    @Before
    public void setUp()
    {
        endpointHandler = new EndpointHandler( "endpoint" )
        {
            @Override
            protected WebResponse doHandle( final WebRequest webRequest, final WebResponse webResponse,
                                            final WebHandlerChain webHandlerChain )
                throws Exception
            {
                return null;
            }
        };
    }

    @Test
    public void testCanHandle()
    {
        final WebRequest webRequest = new WebRequest();
        webRequest.setEndpointPath( "/_/endpoint/a/b" );
        Assert.assertTrue( endpointHandler.canHandle( webRequest ) );
        webRequest.setEndpointPath( "/_/endpoint/" );
        Assert.assertTrue( endpointHandler.canHandle( webRequest ) );
        webRequest.setEndpointPath( "/_/endpoint" );
        Assert.assertTrue( endpointHandler.canHandle( webRequest ) );
        webRequest.setEndpointPath( "/_/otherendpoint" );
        Assert.assertFalse( endpointHandler.canHandle( webRequest ) );
        webRequest.setEndpointPath( "/_/endpointbis" );
        Assert.assertFalse( endpointHandler.canHandle( webRequest ) );
    }

    @Test
    public void testFindRestPath()
    {
        final WebRequest webRequest = new WebRequest();
        webRequest.setEndpointPath( "/_/endpoint/a/b" );
        Assert.assertEquals( "a/b", endpointHandler.findRestPath( webRequest ) );
        webRequest.setEndpointPath( "/_/endpoint/" );
        Assert.assertEquals( "", endpointHandler.findRestPath( webRequest ) );
        webRequest.setEndpointPath( "/_/endpoint" );
        Assert.assertEquals( "", endpointHandler.findRestPath( webRequest ) );
    }
}
