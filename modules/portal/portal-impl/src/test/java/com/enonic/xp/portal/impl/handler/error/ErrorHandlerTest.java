package com.enonic.xp.portal.impl.handler.error;

import org.junit.Test;

import com.enonic.xp.portal.PortalWebRequest;
import com.enonic.xp.portal.handler.BaseWebHandlerTest;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.handler.WebException;
import com.enonic.xp.web.handler.WebResponse;

import static org.junit.Assert.*;

public class ErrorHandlerTest
    extends BaseWebHandlerTest
{
    private ErrorHandler handler;

    @Override
    protected void configure( final PortalWebRequest.Builder requestBuilder )
        throws Exception
    {
        this.handler = new ErrorHandler();

        requestBuilder.method( HttpMethod.GET );
        requestBuilder.endpointPath( "/_/error/401" );
    }

    @Test
    public void testOrder()
    {
        assertEquals( 0, this.handler.getOrder() );
    }

    @Test
    public void testMatch()
    {
        setEndpointPath( null );
        assertEquals( false, this.handler.canHandle( this.request ) );

        setEndpointPath( "/_/other/a/b" );
        assertEquals( false, this.handler.canHandle( this.request ) );

        setEndpointPath( "/error/404" );
        assertEquals( false, this.handler.canHandle( this.request ) );

        setEndpointPath( "/_/error/404" );
        assertEquals( true, this.handler.canHandle( this.request ) );
    }

    @Test
    public void testOptions()
        throws Exception
    {
        setMethod( HttpMethod.OPTIONS );

        final WebResponse res = this.handler.handle( this.request, this.response, null );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( "GET,POST,HEAD,OPTIONS,PUT,DELETE,TRACE", res.getHeaders().get( "Allow" ) );
    }

    @Test
    public void testNoCode()
        throws Exception
    {
        setEndpointPath( "/_/error/other" );

        try
        {
            this.handler.handle( this.request, this.response, null );
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
        setEndpointPath( "/_/error/401" );

        try
        {
            this.handler.handle( this.request, this.response, null );
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
        setEndpointPath( "/_/error/401" );
        this.request.getParams().put( "message", "Some error message" );

        try
        {
            this.handler.handle( this.request, this.response, null );
            fail( "Should throw exception" );
        }
        catch ( final WebException e )
        {
            assertEquals( HttpStatus.UNAUTHORIZED, e.getStatus() );
            assertEquals( "Some error message", e.getMessage() );
        }
    }
}
