package com.enonic.xp.portal.impl.auth;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.auth.IdProviderControllerService;

public class IdProviderResponseWrapperTest
{

    private IdProviderControllerService idProviderControllerService;

    private IdProviderResponseWrapper idProviderResponseWrapper;

    @Before
    public void setup()
        throws IOException
    {
        this.idProviderControllerService = Mockito.mock( IdProviderControllerService.class );
        Mockito.when( idProviderControllerService.execute( Mockito.any() ) ).thenReturn( PortalResponse.create().build() );
        final HttpServletRequest httpServletRequest = Mockito.mock( HttpServletRequest.class );
        final HttpServletResponse httpServletResponse = Mockito.mock( HttpServletResponse.class );

        this.idProviderResponseWrapper =
            new IdProviderResponseWrapper( idProviderControllerService, httpServletRequest, httpServletResponse );
    }

    @Test
    public void testSetStatus()
        throws IOException
    {
        idProviderResponseWrapper.setStatus( 404 );
        Mockito.verify( idProviderControllerService, Mockito.times( 0 ) ).execute( Mockito.any() );
        idProviderResponseWrapper.setStatus( 403 );
        Mockito.verify( idProviderControllerService ).execute( Mockito.any() );
        idProviderResponseWrapper.setStatus( 403 );
        Mockito.verify( idProviderControllerService ).execute( Mockito.any() );
    }

    @Test
    public void testSendError()
        throws IOException
    {
        idProviderResponseWrapper.sendError( 404 );
        Mockito.verify( idProviderControllerService, Mockito.times( 0 ) ).execute( Mockito.any() );
        idProviderResponseWrapper.sendError( 403 );
        Mockito.verify( idProviderControllerService ).execute( Mockito.any() );
        idProviderResponseWrapper.sendError( 403 );
        Mockito.verify( idProviderControllerService ).execute( Mockito.any() );
    }

    @Test
    public void testSendErrorWithMessage()
        throws IOException
    {
        idProviderResponseWrapper.sendError( 404, "message" );
        Mockito.verify( idProviderControllerService, Mockito.times( 0 ) ).execute( Mockito.any() );
        idProviderResponseWrapper.sendError( 403, "message" );
        Mockito.verify( idProviderControllerService ).execute( Mockito.any() );
        idProviderResponseWrapper.sendError( 403, "message" );
        Mockito.verify( idProviderControllerService ).execute( Mockito.any() );
    }

    @Test
    public void testGetWriter()
        throws IOException
    {
        Assert.assertNull( idProviderResponseWrapper.getWriter() );
        idProviderResponseWrapper.setStatus( 403 );
        Assert.assertNotNull( idProviderResponseWrapper.getWriter() );
    }

    @Test
    public void testGetOutputStream()
        throws IOException
    {
        ServletOutputStream outputStream = idProviderResponseWrapper.getOutputStream();
        Assert.assertNull( outputStream );

        idProviderResponseWrapper.setStatus( 403 );

        outputStream = idProviderResponseWrapper.getOutputStream();
        Assert.assertNotNull( outputStream );
        Assert.assertTrue( outputStream.isReady() );
        outputStream.setWriteListener( null );
        outputStream.write( 0 );
    }


}
