package com.enonic.xp.portal.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandlerChain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EndpointHandlerTest
{
    private EndpointHandler endpointHandler;

    @BeforeEach
    void setUp()
    {
        endpointHandler = new EndpointHandler( "endpoint" )
        {
            @Override
            protected WebResponse doHandle( final WebRequest webRequest, final WebResponse webResponse,
                                            final WebHandlerChain webHandlerChain )
            {
                return null;
            }
        };
    }

    @Test
    void testCanHandle()
    {
        final WebRequest webRequest = new WebRequest();
        webRequest.setEndpointPath( "/_/endpoint/a/b" );
        assertTrue( endpointHandler.canHandle( webRequest ) );
        webRequest.setEndpointPath( "/_/endpoint/" );
        assertTrue( endpointHandler.canHandle( webRequest ) );
        webRequest.setEndpointPath( "/_/endpoint" );
        assertTrue( endpointHandler.canHandle( webRequest ) );
        webRequest.setEndpointPath( "/_/otherendpoint" );
        assertFalse( endpointHandler.canHandle( webRequest ) );
        webRequest.setEndpointPath( "/_/endpointbis" );
        assertFalse( endpointHandler.canHandle( webRequest ) );
    }

    @Test
    void testFindRestPath()
    {
        final WebRequest webRequest = new WebRequest();
        webRequest.setEndpointPath( "/_/endpoint/a/b" );
        assertEquals( "a/b", endpointHandler.findRestPath( webRequest ) );
        webRequest.setEndpointPath( "/_/endpoint/" );
        assertEquals( "", endpointHandler.findRestPath( webRequest ) );
        webRequest.setEndpointPath( "/_/endpoint" );
        assertEquals( "", endpointHandler.findRestPath( webRequest ) );
    }

    @Test
    void testPreRestPath()
    {
        final WebRequest webRequest = new WebRequest();
        webRequest.setEndpointPath( "/_/endpoint/a/b" );
        webRequest.setRawPath( "/site/www/_/endpoint/a/b" );

        assertEquals( "/site/www/_/endpoint", endpointHandler.findPreRestPath( webRequest ) );

        webRequest.setEndpointPath( "/_/endpoint/" );
        webRequest.setRawPath( "/site/www/_/endpoint/" );
        assertEquals( "/site/www/_/endpoint", endpointHandler.findPreRestPath( webRequest ) );

        webRequest.setEndpointPath( "/_/endpoint" );
        webRequest.setRawPath( "/site/www/_/endpoint" );
        assertEquals( "/site/www/_/endpoint", endpointHandler.findPreRestPath( webRequest ) );
    }
}
