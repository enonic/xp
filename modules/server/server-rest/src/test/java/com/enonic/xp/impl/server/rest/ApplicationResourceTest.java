package com.enonic.xp.impl.server.rest;

import java.net.URL;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.osgi.framework.Version;

import com.google.common.io.ByteSource;

import jakarta.ws.rs.core.MediaType;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.impl.server.rest.model.ApplicationInstallResultJson;
import com.enonic.xp.impl.server.rest.model.ApplicationInstalledJson;
import com.enonic.xp.jaxrs.impl.JaxRsResourceTestSupport;
import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartItem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;

public class ApplicationResourceTest
    extends JaxRsResourceTestSupport
{
    private ApplicationService applicationService;

    @Override
    protected ApplicationResource getResourceInstance()
    {
        applicationService = mock( ApplicationService.class );

        final ApplicationResource resource = new ApplicationResource();
        resource.setApplicationService( applicationService );

        return resource;
    }

    @Test
    public void install()
        throws Exception
    {

        ApplicationResource resource = getResourceInstance();

        ByteSource byteSource = ByteSource.wrap( "bytes".getBytes() );

        Application application = createApplication();

        MultipartItem multipartItem = mock( MultipartItem.class );
        Mockito.when( multipartItem.getBytes() ).thenReturn( byteSource );
        String fileName = application.getDisplayName();
        Mockito.when( multipartItem.getFileName() ).thenReturn( fileName );

        MultipartForm multipartForm = mock( MultipartForm.class );

        Mockito.when( this.applicationService.installGlobalApplication( any( ByteSource.class ), eq( fileName ) ) ).thenReturn(
            application );

        Mockito.when( multipartForm.get( "file" ) ).thenReturn( multipartItem );

        ApplicationInstallResultJson result = resource.install( multipartForm );

        assertEquals( new ApplicationInstalledJson( application, false ), result.getApplicationInstalledJson() );
    }

    @Test
    public void install_exception()
        throws Exception
    {
        ApplicationResource resource = getResourceInstance();

        MultipartItem multipartItem = mock( MultipartItem.class );

        ByteSource byteSource = ByteSource.wrap( "bytes".getBytes() );
        String fileName = "app-name";

        Mockito.when( multipartItem.getBytes() ).thenReturn( byteSource );
        Mockito.when( multipartItem.getFileName() ).thenReturn( fileName );

        MultipartForm multipartForm = mock( MultipartForm.class );

        Mockito.when( this.applicationService.installGlobalApplication( any( ByteSource.class ), eq( "app-name" ) ) ).thenThrow(
            new RuntimeException() );

        Mockito.when( multipartForm.get( "file" ) ).thenReturn( multipartItem );

        ApplicationInstallResultJson result = resource.install( multipartForm );

        assertEquals( "Failed to process application app-name", result.getFailure() );
    }

    @Test
    public void install_missing_file_item()
        throws Exception
    {
        ApplicationResource resource = getResourceInstance();

        MultipartForm multipartForm = mock( MultipartForm.class );

        final RuntimeException ex = assertThrows( RuntimeException.class, () -> {
            resource.install( multipartForm );
        } );
        assertEquals( "Missing file item", ex.getMessage() );
    }

    @Test
    public void install_url()
        throws Exception
    {
        Application application = createApplication();

        Mockito.when( this.applicationService.installGlobalApplication( eq( new URL( application.getUrl() ) ), any() ) ).thenReturn(
            application );

        String jsonString = request().path( "app/installUrl" ).
            entity( "{\"URL\":\"http://enonic.net\"}", MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "install_url_result.json", jsonString );
    }

    @Test
    public void install_url_not_allowed_protocol()
        throws Exception
    {
        Application application = createApplication( "ftp://enonic.jar" );

        Mockito.when( this.applicationService.installGlobalApplication( new URL( application.getUrl() ) ) ).thenReturn( application );

        String jsonString = request().path( "app/installUrl" ).
            entity( "{\"URL\":\"" + application.getUrl() + "\"}", MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertEquals( "{\"failure\":\"Illegal protocol: ftp\"}", jsonString );
    }

    @Test
    public void install_url_process_error()
        throws Exception
    {
        Application application = createApplication();

        Mockito.when( this.applicationService.installGlobalApplication( eq( new URL( application.getUrl() ) ), any() ) ).thenThrow(
            new RuntimeException() );

        String jsonString = request().path( "app/installUrl" ).
            entity( "{\"URL\":\"" + application.getUrl() + "\"}", MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertEquals( "{\"failure\":\"Failed to process application from http://enonic.net\"}",
                      jsonString );
    }

    @Test
    public void install_invalid_url()
        throws Exception
    {
        Application application = createApplication( "invalid url" );

        String jsonString = request().path( "app/installUrl" ).
            entity( "{\"URL\":\"" + application.getUrl() + "\"}", MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertEquals( "{\"failure\":\"Failed to upload application from invalid url\"}", jsonString );
    }

    @Test
    public void test_start_application()
        throws Exception
    {
        final Application application = createApplication();

        request().path( "app/start" ).
            entity( "{\"key\": \"" + application.getKey() + "\" }", MediaType.APPLICATION_JSON_TYPE ).
            post();

        final InOrder inOrder = Mockito.inOrder( applicationService );
        inOrder.verify( this.applicationService ).startApplication( application.getKey(), true );
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void test_stop_application()
        throws Exception
    {
        final Application application = createApplication();

        request().path( "app/stop" ).
            entity( "{\"key\": \"" + application.getKey() + "\" }", MediaType.APPLICATION_JSON_TYPE ).
            post();

        final InOrder inOrder = Mockito.inOrder( applicationService );
        inOrder.verify( this.applicationService ).stopApplication( application.getKey(), true );
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void test_uninstall_application()
        throws Exception
    {
        final Application application = createApplication();

        request().path( "app/uninstall" ).
            entity( "{\"key\": \"" + application.getKey() + "\" }", MediaType.APPLICATION_JSON_TYPE ).
            post();

        final InOrder inOrder = Mockito.inOrder( applicationService );
        inOrder.verify( this.applicationService ).uninstallApplication( application.getKey(), true );
        inOrder.verifyNoMoreInteractions();
    }

    private Application createApplication()
    {
        return createApplication( "http://enonic.net" );
    }

    private Application createApplication( final String url )
    {
        final Application application = mock( Application.class );
        Mockito.when( application.getKey() ).thenReturn( ApplicationKey.from( "testapplication" ) );
        Mockito.when( application.getVersion() ).thenReturn( new Version( 1, 0, 0 ) );
        Mockito.when( application.getDisplayName() ).thenReturn( "application name" );
        Mockito.when( application.getUrl() ).thenReturn( url );
        Mockito.when( application.getVendorName() ).thenReturn( "Enonic" );
        Mockito.when( application.getVendorUrl() ).thenReturn( "https://www.enonic.com" );
        Mockito.when( application.getMinSystemVersion() ).thenReturn( "5.0" );
        Mockito.when( application.getMaxSystemVersion() ).thenReturn( "5.1" );
        Mockito.when( application.isStarted() ).thenReturn( true );
        Mockito.when( application.getModifiedTime() ).thenReturn( Instant.parse( "2012-01-01T00:00:00.00Z" ) );

        return application;
    }
}
