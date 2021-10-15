package com.enonic.xp.portal.impl.handler.unknown;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UnknownEndpointHandlerTest
{
    private PortalRequest request;

    private UnknownEndpointHandler handler;

    @BeforeEach
    public void setUp()
    {
        this.handler = new UnknownEndpointHandler();
        this.request = new PortalRequest();
    }

    @Test
    public void testCanHandle()
    {
        request.setEndpointPath( null );
        assertFalse( handler.canHandle( request ) );

        request.setEndpointPath( "/unknown" );
        assertTrue( handler.canHandle( request ) );

        request.setEndpointPath( "/_/unknown" );
        assertTrue( handler.canHandle( request ) );
    }

    @Test
    public void testHandle()
    {
        request.setMethod( HttpMethod.GET );
        request.setEndpointPath( "/_/unknown" );

        WebException ex = assertThrows( WebException.class, () -> handler.handle( this.request, PortalResponse.create().build(), null ) );

        assertEquals( "No handler for the [/_/unknown] endpointPath", ex.getMessage() );

        request.setMethod( HttpMethod.PUT );

        ex = assertThrows( WebException.class, () -> handler.handle( this.request, PortalResponse.create().build(), null ) );

        assertEquals( "Method PUT not allowed", ex.getMessage() );
    }

    @Test
    public void testOrder()
    {
        assertTrue( handler.getOrder() > 0 );
    }

}
