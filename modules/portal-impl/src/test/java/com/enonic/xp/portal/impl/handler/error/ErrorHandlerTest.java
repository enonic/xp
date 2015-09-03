package com.enonic.xp.portal.impl.handler.error;

import org.junit.Test;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.PortalException;
import com.enonic.xp.portal.impl.handler.BaseHandlerTest;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;

import static org.junit.Assert.*;

public class ErrorHandlerTest
    extends BaseHandlerTest
{
    private ErrorHandler handler;

    @Override
    protected void configure()
        throws Exception
    {
        this.handler = new ErrorHandler();

        this.request.setMethod( "GET" );
        this.request.setEndpointPath( "/_/asset/error/401" );
    }

    @Test
    public void testOrder()
    {
        assertEquals( 0, this.handler.getOrder() );
    }

    @Test
    public void testMatch()
    {
        this.request.setEndpointPath( null );
        assertEquals( false, this.handler.canHandle( this.request ) );

        this.request.setEndpointPath( "/_/other/a/b" );
        assertEquals( false, this.handler.canHandle( this.request ) );

        this.request.setEndpointPath( "/error/404" );
        assertEquals( false, this.handler.canHandle( this.request ) );

        this.request.setEndpointPath( "/_/error/404" );
        assertEquals( true, this.handler.canHandle( this.request ) );
    }

    @Test
    public void testMethodNotAllowed()
        throws Exception
    {
        assertMetodNotAllowed( this.handler, HttpMethod.POST );
        assertMetodNotAllowed( this.handler, HttpMethod.DELETE );
        assertMetodNotAllowed( this.handler, HttpMethod.PUT );
        assertMetodNotAllowed( this.handler, HttpMethod.TRACE );
    }

    @Test
    public void testOptions()
        throws Exception
    {
        this.request.setMethod( "OPTIONS" );

        final PortalResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( 200, res.getStatus() );
        assertEquals( "GET,HEAD,OPTIONS", res.getHeaders().get( "Allow" ) );
    }

    @Test
    public void testNoCode()
        throws Exception
    {
        this.request.setEndpointPath( "/_/error/other" );

        try
        {
            this.handler.handle( this.request );
            fail( "Should throw exception" );
        }
        catch ( final PortalException e )
        {
            assertEquals( HttpStatus.NOT_FOUND, e.getStatus() );
            assertEquals( HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage() );
        }
    }

    @Test
    public void testNoMessage()
        throws Exception
    {
        this.request.setEndpointPath( "/_/error/401" );

        try
        {
            this.handler.handle( this.request );
            fail( "Should throw exception" );
        }
        catch ( final PortalException e )
        {
            assertEquals( HttpStatus.UNAUTHORIZED, e.getStatus() );
            assertEquals( HttpStatus.UNAUTHORIZED.getReasonPhrase(), e.getMessage() );
        }
    }

    @Test
    public void testWithMessage()
        throws Exception
    {
        this.request.setEndpointPath( "/_/error/401" );
        this.request.getParams().put( "message", "Some error message" );

        try
        {
            this.handler.handle( this.request );
            fail( "Should throw exception" );
        }
        catch ( final PortalException e )
        {
            assertEquals( HttpStatus.UNAUTHORIZED, e.getStatus() );
            assertEquals( "Some error message", e.getMessage() );
        }
    }
}
