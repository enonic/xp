package com.enonic.xp.impl.server.rest;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.impl.server.rest.model.ApplicationInfoJson;
import com.enonic.xp.impl.server.rest.model.ApplicationActionResultJson;
import com.enonic.xp.impl.server.rest.model.ApplicationInstallParams;
import com.enonic.xp.impl.server.rest.model.ApplicationParams;
import com.enonic.xp.util.Version;
import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartItem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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
        final ApplicationDescriptorService applicationDescriptorService = mock( ApplicationDescriptorService.class );
        service = new ApplicationResourceService( applicationService, applicationDescriptorService, _ -> {
        } );
        service.applicationLoader = mock( ApplicationLoader.class );
    }

    @Test
    void install()
    {
        final Application application = createApplication();
        when( applicationService.installGlobalApplication( any( ByteSource.class ) ) ).thenReturn( application );

        final MultipartForm form = mockMultipartForm( ByteSource.wrap( "bytes".getBytes() ), "app-name" );
        final ApplicationInfoJson result = service.install( form );

        assertNotNull( result );
        assertEquals( "testapplication", result.getKey() );
        assertFalse( result.getLocal() );
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
        assertThrows( RuntimeException.class, () -> service.install( form ) );
    }

    @Test
    void installUrl()
    {
        final Application application = createApplication();
        when( applicationService.installGlobalApplication( any( ByteSource.class ) ) ).thenReturn( application );
        when( service.applicationLoader.load( any( String.class ), any(), any() ) ).thenReturn( ByteSource.wrap( "bytes".getBytes() ) );

        final ApplicationInstallParams params = createParams( "https://enonic.net", null );
        final ApplicationInfoJson result = service.installUrl( params );

        assertNotNull( result );
        assertEquals( "testapplication", result.getKey() );
        assertFalse( result.getLocal() );
    }

    @Test
    void installUrl_error()
    {
        when( service.applicationLoader.load( any( String.class ), any(), any() ) ).thenThrow( new RuntimeException( "Test error" ) );

        final ApplicationInstallParams params = createParams( "https://enonic.net", null );
        assertThrows( RuntimeException.class, () -> service.installUrl( params ) );
    }

    @Test
    void start()
    {
        final ApplicationParams params = createApplicationParams( "com.enonic.app1", "com.enonic.app2" );
        final ApplicationActionResultJson result = service.start( params );

        verify( applicationService ).startApplication( ApplicationKey.from( "com.enonic.app1" ) );
        verify( applicationService ).startApplication( ApplicationKey.from( "com.enonic.app2" ) );
        assertThat( result.getResults() ).containsExactlyInAnyOrder( new ApplicationActionResultJson.ActionResult( "com.enonic.app1", true ),
                                                            new ApplicationActionResultJson.ActionResult( "com.enonic.app2", true ) );
    }

    @Test
    void stop()
    {
        final ApplicationParams params = createApplicationParams( "com.enonic.app1", "com.enonic.app2" );
        final ApplicationActionResultJson result = service.stop( params );

        verify( applicationService ).stopApplication( ApplicationKey.from( "com.enonic.app1" ) );
        verify( applicationService ).stopApplication( ApplicationKey.from( "com.enonic.app2" ) );
        assertThat( result.getResults() ).containsExactlyInAnyOrder( new ApplicationActionResultJson.ActionResult( "com.enonic.app1", true ),
                                                            new ApplicationActionResultJson.ActionResult( "com.enonic.app2", true ) );
    }

    @Test
    void uninstall()
    {
        final ApplicationParams params = createApplicationParams( "com.enonic.app1", "com.enonic.app2" );
        final ApplicationActionResultJson result = service.uninstall( params );

        verify( applicationService ).uninstallApplication( ApplicationKey.from( "com.enonic.app1" ) );
        verify( applicationService ).uninstallApplication( ApplicationKey.from( "com.enonic.app2" ) );
        assertThat( result.getResults() ).containsExactlyInAnyOrder( new ApplicationActionResultJson.ActionResult( "com.enonic.app1", true ),
                                                            new ApplicationActionResultJson.ActionResult( "com.enonic.app2", true ) );
    }

    @Test
    void start_failure()
    {
        doThrow( new RuntimeException( "start failed" ) ).when( applicationService )
            .startApplication( ApplicationKey.from( "com.enonic.app2" ) );

        final ApplicationParams params = createApplicationParams( "com.enonic.app1", "com.enonic.app2" );
        final ApplicationActionResultJson result = service.start( params );

        assertThat( result.getResults() ).containsExactlyInAnyOrder( new ApplicationActionResultJson.ActionResult( "com.enonic.app1", true ),
                                                            new ApplicationActionResultJson.ActionResult( "com.enonic.app2", false ) );
    }

    @Test
    void stop_failure()
    {
        doThrow( new RuntimeException( "stop failed" ) ).when( applicationService )
            .stopApplication( ApplicationKey.from( "com.enonic.app2" ) );

        final ApplicationParams params = createApplicationParams( "com.enonic.app1", "com.enonic.app2" );
        final ApplicationActionResultJson result = service.stop( params );

        assertThat( result.getResults() ).containsExactlyInAnyOrder( new ApplicationActionResultJson.ActionResult( "com.enonic.app1", true ),
                                                            new ApplicationActionResultJson.ActionResult( "com.enonic.app2", false ) );
    }

    @Test
    void uninstall_failure()
    {
        doThrow( new RuntimeException( "uninstall failed" ) ).when( applicationService )
            .uninstallApplication( ApplicationKey.from( "com.enonic.app2" ) );

        final ApplicationParams params = createApplicationParams( "com.enonic.app1", "com.enonic.app2" );
        final ApplicationActionResultJson result = service.uninstall( params );

        assertThat( result.getResults() ).containsExactlyInAnyOrder( new ApplicationActionResultJson.ActionResult( "com.enonic.app1", true ),
                                                            new ApplicationActionResultJson.ActionResult( "com.enonic.app2", false ) );
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
        when( application.getMinSystemVersion() ).thenReturn( "5.0" );
        when( application.getMaxSystemVersion() ).thenReturn( "5.1" );
        when( application.isStarted() ).thenReturn( true );
        when( application.getModifiedTime() ).thenReturn( Instant.parse( "2012-01-01T00:00:00.00Z" ) );
        return application;
    }
}
