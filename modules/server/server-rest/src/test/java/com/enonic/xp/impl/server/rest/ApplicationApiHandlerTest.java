package com.enonic.xp.impl.server.rest;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.impl.server.rest.model.ApplicationInstallResultJson;
import com.enonic.xp.impl.server.rest.model.ApplicationInstalledJson;
import com.enonic.xp.util.Version;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApplicationApiHandlerTest
{
    private ApplicationResourceService applicationResourceService;

    private ApplicationApiHandler handler;

    @BeforeEach
    void setUp()
    {
        applicationResourceService = mock( ApplicationResourceService.class );
        handler = new ApplicationApiHandler( applicationResourceService );
    }

    @Test
    void handleMethodNotAllowed()
    {
        final WebRequest request = new WebRequest();
        request.setMethod( HttpMethod.GET );
        request.setEndpointPath( "/installUrl" );

        final WebResponse response = handler.handle( request );

        assertEquals( HttpStatus.METHOD_NOT_ALLOWED, response.getStatus() );
    }

    @Test
    void handleNotFound()
    {
        final WebRequest request = new WebRequest();
        request.setMethod( HttpMethod.POST );
        request.setEndpointPath( "/unknown" );

        final WebResponse response = handler.handle( request );

        assertEquals( HttpStatus.NOT_FOUND, response.getStatus() );
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
        request.setEndpointPath( "/installUrl" );
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
        request.setEndpointPath( "/installUrl" );
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
        request.setEndpointPath( "/installUrl" );
        request.setBody( "not json" );

        final WebResponse response = handler.handle( request );

        assertEquals( HttpStatus.BAD_REQUEST, response.getStatus() );
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
