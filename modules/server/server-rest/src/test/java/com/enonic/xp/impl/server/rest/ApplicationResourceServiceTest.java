package com.enonic.xp.impl.server.rest;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.impl.server.rest.model.ApplicationInstallParams;
import com.enonic.xp.impl.server.rest.model.ApplicationInstallResultJson;
import com.enonic.xp.impl.server.rest.model.ApplicationParams;
import com.enonic.xp.util.Version;
import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartItem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApplicationResourceServiceTest
{
    private ApplicationService applicationService;

    private ApplicationResourceService service;

    @BeforeEach
    void setUp()
    {
        applicationService = mock( ApplicationService.class );
        service = new ApplicationResourceService( applicationService, _ -> {
        } );
        service.applicationLoader = mock( ApplicationLoader.class );
    }

    @Test
    void install()
    {
        final Application application = createApplication();
        when( applicationService.installGlobalApplication( any( ByteSource.class ) ) ).thenReturn( application );

        final MultipartForm form = mockMultipartForm( ByteSource.wrap( "bytes".getBytes() ), "app-name" );
        final ApplicationInstallResultJson result = service.install( form );

        assertNull( result.getFailure() );
        assertNotNull( result.getApplicationInstalledJson() );
        assertEquals( "testapplication", result.getApplicationInstalledJson().getApplication().getKey() );
        assertFalse( result.getApplicationInstalledJson().getApplication().getLocal() );
    }

    @Test
    void install_missing_file()
    {
        final MultipartForm form = mock( MultipartForm.class );

        final RuntimeException ex = assertThrows( RuntimeException.class, () -> service.install( form ) );
        assertEquals( "Missing file item", ex.getMessage() );
    }

    @Test
    void install_error()
    {
        when( applicationService.installGlobalApplication( any( ByteSource.class ) ) ).thenThrow( new RuntimeException() );

        final MultipartForm form = mockMultipartForm( ByteSource.wrap( "bytes".getBytes() ), "app-name" );
        final ApplicationInstallResultJson result = service.install( form );

        assertEquals( "Failed to process application app-name", result.getFailure() );
        assertNull( result.getApplicationInstalledJson() );
    }

    @Test
    void installUrl()
    {
        final Application application = createApplication();
        when( applicationService.installGlobalApplication( any( ByteSource.class ) ) ).thenReturn( application );
        when( service.applicationLoader.load( any( String.class ), any(), any() ) ).thenReturn( ByteSource.wrap( "bytes".getBytes() ) );

        final ApplicationInstallParams params = createParams( "https://enonic.net", null );
        final ApplicationInstallResultJson result = service.installUrl( params );

        assertNull( result.getFailure() );
        assertNotNull( result.getApplicationInstalledJson() );
        assertEquals( "testapplication", result.getApplicationInstalledJson().getApplication().getKey() );
        assertFalse( result.getApplicationInstalledJson().getApplication().getLocal() );
    }

    @Test
    void installUrl_error()
    {
        when( service.applicationLoader.load( any( String.class ), any(), any() ) ).thenThrow( new RuntimeException( "Test error" ) );

        final ApplicationInstallParams params = createParams( "https://enonic.net", null );
        final ApplicationInstallResultJson result = service.installUrl( params );

        assertEquals( "Failed to upload application from https://enonic.net", result.getFailure() );
        assertNull( result.getApplicationInstalledJson() );
    }

    @Test
    void start()
    {
        final ApplicationParams params = createApplicationParams( "com.enonic.app1", "com.enonic.app2" );
        service.start( params );

        verify( applicationService ).startApplication( ApplicationKey.from( "com.enonic.app1" ) );
        verify( applicationService ).startApplication( ApplicationKey.from( "com.enonic.app2" ) );
    }

    @Test
    void stop()
    {
        final ApplicationParams params = createApplicationParams( "com.enonic.app1", "com.enonic.app2" );
        service.stop( params );

        verify( applicationService ).stopApplication( ApplicationKey.from( "com.enonic.app1" ) );
        verify( applicationService ).stopApplication( ApplicationKey.from( "com.enonic.app2" ) );
    }

    @Test
    void uninstall()
    {
        final ApplicationParams params = createApplicationParams( "com.enonic.app1", "com.enonic.app2" );
        service.uninstall( params );

        verify( applicationService ).uninstallApplication( ApplicationKey.from( "com.enonic.app1" ) );
        verify( applicationService ).uninstallApplication( ApplicationKey.from( "com.enonic.app2" ) );
    }

    private static ApplicationParams createApplicationParams( final String... keys )
    {
        final ApplicationParams params = new ApplicationParams();
        params.setKey( java.util.Set.of( keys ) );
        return params;
    }

    private static ApplicationInstallParams createParams( final String url, final String sha512 )
    {
        final com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        final com.fasterxml.jackson.databind.node.ObjectNode node = mapper.createObjectNode();
        node.put( "URL", url );
        if ( sha512 != null )
        {
            node.put( "sha512", sha512 );
        }
        try
        {
            return mapper.treeToValue( node, ApplicationInstallParams.class );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private static MultipartForm mockMultipartForm( final ByteSource byteSource, final String fileName )
    {
        final MultipartItem multipartItem = mock( MultipartItem.class );
        when( multipartItem.getBytes() ).thenReturn( byteSource );
        when( multipartItem.getFileName() ).thenReturn( fileName );

        final MultipartForm form = mock( MultipartForm.class );
        when( form.get( "file" ) ).thenReturn( multipartItem );
        return form;
    }

    private Application createApplication()
    {
        final Application application = mock( Application.class );
        when( application.getKey() ).thenReturn( ApplicationKey.from( "testapplication" ) );
        when( application.getVersion() ).thenReturn( Version.parseVersion( "1.0.0" ) );
        when( application.getDisplayName() ).thenReturn( "application name" );
        when( application.getUrl() ).thenReturn( "https://enonic.net" );
        when( application.getVendorName() ).thenReturn( "Enonic" );
        when( application.getVendorUrl() ).thenReturn( "https://www.enonic.com" );
        when( application.getMinSystemVersion() ).thenReturn( "5.0" );
        when( application.getMaxSystemVersion() ).thenReturn( "5.1" );
        when( application.isStarted() ).thenReturn( true );
        when( application.getModifiedTime() ).thenReturn( Instant.parse( "2012-01-01T00:00:00.00Z" ) );
        return application;
    }
}
