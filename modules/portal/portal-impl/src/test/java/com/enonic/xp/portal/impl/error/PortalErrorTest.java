package com.enonic.xp.portal.impl.error;

import org.junit.jupiter.api.Test;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.web.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class PortalErrorTest
{

    @Test
    void testGetStatus()
    {
        final PortalRequest request = new PortalRequest();
        final PortalError error = PortalError.create().
            request( request ).
            status( HttpStatus.INTERNAL_SERVER_ERROR ).
            build();
        assertEquals( HttpStatus.INTERNAL_SERVER_ERROR, error.getStatus() );
    }

    @Test
    void testGetRequest()
    {
        final PortalRequest request = new PortalRequest();
        final PortalError error = PortalError.create().
            request( request ).
            status( HttpStatus.INTERNAL_SERVER_ERROR ).
            build();
        assertSame( request, error.getRequest() );
    }

    @Test
    void testGetMessage()
    {
        final PortalRequest request = new PortalRequest();
        final PortalError error = PortalError.create().
            request( request ).
            status( HttpStatus.INTERNAL_SERVER_ERROR ).
            build();

        final PortalError error2 = PortalError.create().
            request( request ).
            status( HttpStatus.INTERNAL_SERVER_ERROR ).
            message( "Some error" ).
            build();

        assertEquals( "", error.getMessage() );
        assertEquals( "Some error", error2.getMessage() );
    }

    @Test
    void testGetException()
    {
        final Exception exception = new Exception( "my exception" );
        final PortalRequest request = new PortalRequest();
        final PortalError error = PortalError.create().
            request( request ).
            status( HttpStatus.INTERNAL_SERVER_ERROR ).
            exception( exception ).
            build();
        assertSame( exception, error.getException() );
    }

    @Test
    void testCopy()
    {
        final Exception exception = new Exception( "my exception" );
        final PortalRequest request = new PortalRequest();
        final PortalError error = PortalError.create().
            request( request ).
            status( HttpStatus.INTERNAL_SERVER_ERROR ).
            exception( exception ).
            message( "Some error" ).
            build();

        final PortalError errorCopy = PortalError.create( error ).build();
        assertSame( errorCopy.getException(), errorCopy.getException() );
        assertEquals( errorCopy.getMessage(), errorCopy.getMessage() );
        assertSame( errorCopy.getRequest(), errorCopy.getRequest() );
        assertEquals( errorCopy.getStatus(), errorCopy.getStatus() );
    }
}
