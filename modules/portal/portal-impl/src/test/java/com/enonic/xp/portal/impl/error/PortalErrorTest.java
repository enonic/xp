package com.enonic.xp.portal.impl.error;

import org.junit.Test;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.web.HttpStatus;

import static org.junit.Assert.*;

public class PortalErrorTest
{

    @Test
    public void testGetStatus()
        throws Exception
    {
        final PortalRequest request = new PortalRequest();
        final PortalError error = PortalError.create().
            request( request ).
            status( HttpStatus.INTERNAL_SERVER_ERROR ).
            build();
        assertEquals( HttpStatus.INTERNAL_SERVER_ERROR, error.getStatus() );
    }

    @Test
    public void testGetRequest()
        throws Exception
    {
        final PortalRequest request = new PortalRequest();
        final PortalError error = PortalError.create().
            request( request ).
            status( HttpStatus.INTERNAL_SERVER_ERROR ).
            build();
        assertSame( request, error.getRequest() );
    }

    @Test
    public void testGetMessage()
        throws Exception
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
    public void testGetException()
        throws Exception
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
    public void testCopy()
        throws Exception
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