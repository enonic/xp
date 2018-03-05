package com.enonic.xp.portal.impl.handler.error;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.BaseHandlerTest;

import static org.junit.Assert.*;

public class ErrorHandlerTest
    extends BaseHandlerTest
{
    private ErrorHandler handler;

    private PortalRequest request;

    @Before
    public final void setup()
        throws Exception
    {
        this.request = new PortalRequest();
        this.handler = new ErrorHandler();

        this.request.setMethod( HttpMethod.GET );
        this.request.setEndpointPath( "/_/error/401" );
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
    public void testOptions()
        throws Exception
    {
        this.request.setEndpointPath( "/_/error/401" );
        this.request.setMethod( HttpMethod.OPTIONS );

        final WebResponse res = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( "GET,POST,HEAD,OPTIONS,PUT,DELETE,TRACE", res.getHeaders().get( "Allow" ) );
    }

    @Test
    public void testNoCode()
        throws Exception
    {
        this.request.setEndpointPath( "/_/error/other" );

        try
        {
            this.handler.handle( this.request, PortalResponse.create().build(), null );
            fail( "Should throw exception" );
        }
        catch ( final WebException e )
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
            this.handler.handle( this.request, PortalResponse.create().build(), null );
            fail( "Should throw exception" );
        }
        catch ( final WebException e )
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
            this.handler.handle( this.request, PortalResponse.create().build(), null );
            fail( "Should throw exception" );
        }
        catch ( final WebException e )
        {
            assertEquals( HttpStatus.UNAUTHORIZED, e.getStatus() );
            assertEquals( "Some error message", e.getMessage() );
        }
    }
}
