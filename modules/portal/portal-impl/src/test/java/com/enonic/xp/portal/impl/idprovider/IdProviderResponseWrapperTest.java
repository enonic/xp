package com.enonic.xp.portal.impl.idprovider;

import java.io.IOException;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.idprovider.IdProviderControllerService;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.web.vhost.IdProviderFlow;
import com.enonic.xp.web.vhost.VirtualHost;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IdProviderResponseWrapperTest
{

    private IdProviderControllerService idProviderControllerService;

    private IdProviderResponseWrapper idProviderResponseWrapper;

    @BeforeEach
    void setup()
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
    void testSetStatus()
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
    void testSendError()
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
    void testSendErrorWithMessage()
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
    void testHandle401_gatedByLoginFlow()
        throws IOException
    {
        final HttpServletRequest httpServletRequest = Mockito.mock( HttpServletRequest.class );
        final HttpServletResponse httpServletResponse = Mockito.mock( HttpServletResponse.class );

        final VirtualHost virtualHost = Mockito.mock( VirtualHost.class );
        Mockito.when( virtualHost.getDefaultIdProviderKey() ).thenReturn( IdProviderKey.system() );
        Mockito.when( virtualHost.getIdProviderFlows( IdProviderKey.system() ) )
            .thenReturn( Set.of( IdProviderFlow.AUTOLOGIN ) );
        Mockito.when( httpServletRequest.getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        final IdProviderResponseWrapper responseWrapper =
            new IdProviderResponseWrapper( idProviderControllerService, httpServletRequest, httpServletResponse );

        // the default id provider does not have the login flow, so handle401 must not run
        responseWrapper.sendError( 401 );
        Mockito.verify( idProviderControllerService, Mockito.times( 0 ) ).execute( Mockito.any() );
        Mockito.verify( httpServletResponse ).sendError( 401 );

        Mockito.when( virtualHost.getIdProviderFlows( IdProviderKey.system() ) )
            .thenReturn( Set.of( IdProviderFlow.LOGIN, IdProviderFlow.AUTOLOGIN ) );

        responseWrapper.sendError( 401 );
        Mockito.verify( idProviderControllerService ).execute( Mockito.any() );
    }

    @Test
    void testGetWriter()
        throws IOException
    {
        assertNull( idProviderResponseWrapper.getWriter() );
        idProviderResponseWrapper.setStatus( 403 );
        assertNotNull( idProviderResponseWrapper.getWriter() );
    }

    @Test
    void testGetOutputStream()
        throws IOException
    {
        ServletOutputStream outputStream = idProviderResponseWrapper.getOutputStream();
        assertNull( outputStream );

        idProviderResponseWrapper.setStatus( 403 );

        outputStream = idProviderResponseWrapper.getOutputStream();
        assertNotNull( outputStream );
        assertTrue( outputStream.isReady() );
        outputStream.setWriteListener( null );
        outputStream.write( 0 );
    }


}
