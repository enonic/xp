package com.enonic.xp.impl.server.rest;

import java.time.Instant;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.impl.server.rest.model.ApplicationInstallResultJson;
import com.enonic.xp.impl.server.rest.model.ApplicationInstalledJson;
import com.enonic.xp.impl.server.rest.model.ApplicationParams;
import com.enonic.xp.util.Version;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApplicationApiHandlerTest
{
    private ApplicationResourceService applicationResourceService;

    private MultipartService multipartService;

    private ApplicationApiHandler handler;

    @BeforeEach
    void setUp()
    {
        applicationResourceService = mock( ApplicationResourceService.class );
        multipartService = mock( MultipartService.class );
        handler = new ApplicationApiHandler( applicationResourceService, multipartService );
    }

    @Test
    void handleMethodNotAllowed()
    {
        final WebRequest request = new WebRequest();
        request.setMethod( HttpMethod.PUT );
        request.setRawPath( "/_/server:app/installUrl" );

        final WebResponse response = handler.handle( request );

        assertEquals( HttpStatus.METHOD_NOT_ALLOWED, response.getStatus() );
    }

    @Test
    void handleNotFound()
    {
        final WebRequest request = new WebRequest();
        request.setMethod( HttpMethod.POST );
        request.setRawPath( "/_/server:app/unknown" );

        final WebResponse response = handler.handle( request );

        assertEquals( HttpStatus.NOT_FOUND, response.getStatus() );
    }

    @Test
    void handleInstall()
    {
        final Application application = createApplication();
        final ApplicationInstallResultJson resultJson = new ApplicationInstallResultJson();
        resultJson.setApplicationInstalledJson( new ApplicationInstalledJson( application, false ) );

        final MultipartForm form = mock( MultipartForm.class );
        when( multipartService.parse( any() ) ).thenReturn( form );
        when( applicationResourceService.install( form ) ).thenReturn( resultJson );

        final WebRequest request = new WebRequest();
        request.setMethod( HttpMethod.POST );
        request.setRawPath( "/_/server:app/install" );

        final WebResponse response = handler.handle( request );

        assertEquals( HttpStatus.OK, response.getStatus() );
        verify( applicationResourceService ).install( form );
    }

    @Test
    void handleInstallUrl()
    {
        final Application application = createApplication();
        final ApplicationInstallResultJson resultJson = new ApplicationInstallResultJson();
        resultJson.setApplicationInstalledJson( new ApplicationInstalledJson( application, false ) );
        when( applicationResourceService.installUrl( any() ) ).thenReturn( resultJson );

        final WebRequest request = new WebRequest();
        request.setMethod( HttpMethod.POST );
        request.setRawPath( "/_/server:app/installUrl" );
        request.setBody( "{\"URL\":\"https://enonic.net\"}" );

        final WebResponse response = handler.handle( request );

        assertEquals( HttpStatus.OK, response.getStatus() );
        final String body = response.getBody().toString();
        assertTrue( body.contains( "\"key\":\"testapplication\"" ) );
        assertTrue( body.contains( "\"applicationInstalledJson\"" ) );
    }

    @Test
    void handleInstallUrlFailure()
    {
        final ApplicationInstallResultJson resultJson = new ApplicationInstallResultJson();
        resultJson.setFailure( "Failed to upload application from https://enonic.net" );
        when( applicationResourceService.installUrl( any() ) ).thenReturn( resultJson );

        final WebRequest request = new WebRequest();
        request.setMethod( HttpMethod.POST );
        request.setRawPath( "/_/server:app/installUrl" );
        request.setBody( "{\"URL\":\"https://enonic.net\"}" );

        final WebResponse response = handler.handle( request );

        assertEquals( HttpStatus.OK, response.getStatus() );
        final String body = response.getBody().toString();
        assertTrue( body.contains( "\"failure\"" ) );
    }

    @Test
    void handleInstallUrlBadRequest()
    {
        final WebRequest request = new WebRequest();
        request.setMethod( HttpMethod.POST );
        request.setRawPath( "/_/server:app/installUrl" );
        request.setBody( "not json" );

        final WebResponse response = handler.handle( request );

        assertEquals( HttpStatus.BAD_REQUEST, response.getStatus() );
    }

    @Test
    void handleStart()
    {
        final WebRequest request = new WebRequest();
        request.setMethod( HttpMethod.POST );
        request.setRawPath( "/_/server:app/start" );
        request.setBody( "{\"key\":\"testapplication\"}" );

        final WebResponse response = handler.handle( request );

        assertEquals( HttpStatus.OK, response.getStatus() );
        verify( applicationResourceService ).start( any() );
    }

    @Test
    void handleStop()
    {
        final WebRequest request = new WebRequest();
        request.setMethod( HttpMethod.POST );
        request.setRawPath( "/_/server:app/stop" );
        request.setBody( "{\"key\":\"testapplication\"}" );

        final WebResponse response = handler.handle( request );

        assertEquals( HttpStatus.OK, response.getStatus() );
        verify( applicationResourceService ).stop( any() );
    }

    @Test
    void handleUninstall()
    {
        final WebRequest request = new WebRequest();
        request.setMethod( HttpMethod.POST );
        request.setRawPath( "/_/server:app/uninstall" );
        request.setBody( "{\"key\":\"testapplication\"}" );

        final WebResponse response = handler.handle( request );

        assertEquals( HttpStatus.OK, response.getStatus() );
        final ArgumentCaptor<ApplicationParams> captor = ArgumentCaptor.forClass( ApplicationParams.class );
        verify( applicationResourceService ).uninstall( captor.capture() );
        assertEquals( Set.of( "testapplication" ), captor.getValue().getKey() );
    }

    @Test
    void handleUninstallMultiple()
    {
        final WebRequest request = new WebRequest();
        request.setMethod( HttpMethod.POST );
        request.setRawPath( "/_/server:app/uninstall" );
        request.setBody( "{\"key\":[\"app1\",\"app2\"]}" );

        final WebResponse response = handler.handle( request );

        assertEquals( HttpStatus.OK, response.getStatus() );
        final ArgumentCaptor<ApplicationParams> captor = ArgumentCaptor.forClass( ApplicationParams.class );
        verify( applicationResourceService ).uninstall( captor.capture() );
        assertEquals( Set.of( "app1", "app2" ), captor.getValue().getKey() );
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
