package com.enonic.xp.impl.server.rest;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import jakarta.ws.rs.core.MediaType;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.impl.server.rest.model.ApplicationInstallResultJson;
import com.enonic.xp.impl.server.rest.model.ApplicationInstalledJson;
import com.enonic.xp.jaxrs.impl.JaxRsResourceTestSupport;
import com.enonic.xp.util.Version;
import com.enonic.xp.web.multipart.MultipartForm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApplicationResourceTest
    extends JaxRsResourceTestSupport
{
    private ApplicationResourceService applicationResourceService;

    @Override
    protected ApplicationResource getResourceInstance()
    {
        applicationResourceService = mock( ApplicationResourceService.class );

        return new ApplicationResource( applicationResourceService );
    }

    @Test
    void install()
    {
        ApplicationResource resource = getResourceInstance();

        MultipartForm multipartForm = mock( MultipartForm.class );

        resource.install( multipartForm );

        final InOrder inOrder = Mockito.inOrder( applicationResourceService );
        inOrder.verify( applicationResourceService ).install( multipartForm );
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void install_url()
        throws Exception
    {
        final Application application = createApplication();
        final ApplicationInstallResultJson resultJson = new ApplicationInstallResultJson();
        resultJson.setApplicationInstalledJson( new ApplicationInstalledJson( application, false ) );
        when( this.applicationResourceService.installUrl( any() ) ).thenReturn( resultJson );

        String jsonString = request().path( "app/installUrl" )
            .entity( "{\"URL\":\"https://enonic.net\"}", MediaType.APPLICATION_JSON_TYPE )
            .post()
            .getAsString();

        assertJson( "install_url_result.json", jsonString );
    }

    @Test
    void install_url_process_error()
        throws Exception
    {
        final ApplicationInstallResultJson resultJson = new ApplicationInstallResultJson();
        resultJson.setFailure( "Failed to upload application from https://enonic.net" );
        when( this.applicationResourceService.installUrl( any() ) ).thenReturn( resultJson );

        String jsonString = request().path( "app/installUrl" )
            .entity( "{\"URL\":\"https://enonic.net\"}", MediaType.APPLICATION_JSON_TYPE )
            .post()
            .getAsString();

        assertEquals( "{\"failure\":\"Failed to upload application from https://enonic.net\"}", jsonString );
    }

    @Test
    void install_invalid_url()
        throws Exception
    {
        final ApplicationInstallResultJson resultJson = new ApplicationInstallResultJson();
        resultJson.setFailure( "Failed to upload application from invalid url" );
        when( this.applicationResourceService.installUrl( any() ) ).thenReturn( resultJson );

        String jsonString =
            request().path( "app/installUrl" ).entity( "{\"URL\":\"invalid url\"}", MediaType.APPLICATION_JSON_TYPE ).post().getAsString();

        assertEquals( "{\"failure\":\"Failed to upload application from invalid url\"}", jsonString );
    }

    @Test
    void test_start_application()
        throws Exception
    {
        final Application application = createApplication();

        request().path( "app/start" ).entity( "{\"key\": \"" + application.getKey() + "\" }", MediaType.APPLICATION_JSON_TYPE ).post();

        final InOrder inOrder = Mockito.inOrder( applicationResourceService );
        inOrder.verify( this.applicationResourceService ).start( any() );
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void test_stop_application()
        throws Exception
    {
        final Application application = createApplication();

        request().path( "app/stop" ).entity( "{\"key\": \"" + application.getKey() + "\" }", MediaType.APPLICATION_JSON_TYPE ).post();

        final InOrder inOrder = Mockito.inOrder( applicationResourceService );
        inOrder.verify( this.applicationResourceService ).stop( any() );
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void test_uninstall_application()
        throws Exception
    {
        final Application application = createApplication();

        request().path( "app/uninstall" ).entity( "{\"key\": \"" + application.getKey() + "\" }", MediaType.APPLICATION_JSON_TYPE ).post();

        final InOrder inOrder = Mockito.inOrder( applicationResourceService );
        inOrder.verify( this.applicationResourceService ).uninstall( any() );
        inOrder.verifyNoMoreInteractions();
    }

    private Application createApplication()
    {
        return createApplication( "https://enonic.net" );
    }

    private Application createApplication( final String url )
    {
        final Application application = mock( Application.class );
        when( application.getKey() ).thenReturn( ApplicationKey.from( "testapplication" ) );
        when( application.getVersion() ).thenReturn( Version.parseVersion( "1.0.0" ) );
        when( application.getDisplayName() ).thenReturn( "application name" );
        when( application.getUrl() ).thenReturn( url );
        when( application.getVendorName() ).thenReturn( "Enonic" );
        when( application.getVendorUrl() ).thenReturn( "https://www.enonic.com" );
        when( application.getMinSystemVersion() ).thenReturn( "5.0" );
        when( application.getMaxSystemVersion() ).thenReturn( "5.1" );
        when( application.isStarted() ).thenReturn( true );
        when( application.getModifiedTime() ).thenReturn( Instant.parse( "2012-01-01T00:00:00.00Z" ) );

        return application;
    }
}
