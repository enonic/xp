package com.enonic.xp.portal.impl.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class ErrorHandlerTest
{
    private ErrorHandler handler;

    private PortalRequest request;

    @BeforeEach
    public final void setup()
    {
        this.request = new PortalRequest();
        this.handler = new ErrorHandler();

        this.request.setMethod( HttpMethod.GET );
        this.request.setEndpointPath( "/_/error/401" );
    }

    @Test
    void testOptions()
        throws Exception
    {
        this.request.setEndpointPath( "/_/error/401" );
        this.request.setMethod( HttpMethod.OPTIONS );

        final WebResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( "GET,POST,HEAD,OPTIONS,PUT,DELETE,TRACE,PATCH", res.getHeaders().get( "Allow" ) );
    }

    @Test
    void testNoCode()
        throws Exception
    {
        this.request.setEndpointPath( "/_/error/other" );

        try
        {
            this.handler.handle( this.request );
            fail( "Should throw exception" );
        }
        catch ( final WebException e )
        {
            assertEquals( HttpStatus.NOT_FOUND, e.getStatus() );
            assertEquals( HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage() );
        }
    }

    @Test
    void testNoMessage()
        throws Exception
    {
        this.request.setEndpointPath( "/_/error/401" );

        try
        {
            this.handler.handle( this.request );
            fail( "Should throw exception" );
        }
        catch ( final WebException e )
        {
            assertEquals( HttpStatus.UNAUTHORIZED, e.getStatus() );
            assertEquals( HttpStatus.UNAUTHORIZED.getReasonPhrase(), e.getMessage() );
        }
    }

    @Test
    void testWithMessage()
        throws Exception
    {
        this.request.setEndpointPath( "/_/error/401" );
        this.request.getParams().put( "message", "Some error message" );

        try
        {
            this.handler.handle( this.request );
            fail( "Should throw exception" );
        }
        catch ( final WebException e )
        {
            assertEquals( HttpStatus.UNAUTHORIZED, e.getStatus() );
            assertEquals( "Some error message", e.getMessage() );
        }
    }

    @Test
    void testHandleMethodNotAllowed()
    {
        this.request.setMethod( HttpMethod.CONNECT );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( this.request ) );
        assertEquals( HttpStatus.METHOD_NOT_ALLOWED, ex.getStatus() );
        assertEquals( "Method CONNECT not allowed", ex.getMessage() );
    }
}
