package com.enonic.xp.impl.server.rest;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import jakarta.ws.rs.core.MediaType;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.impl.server.rest.model.ApplicationInstallResultJson;
import com.enonic.xp.jaxrs.impl.JaxRsResourceTestSupport;
import com.enonic.xp.util.Version;
import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartItem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApplicationResourceTest
    extends JaxRsResourceTestSupport
{
    private ApplicationService applicationService;

    @Override
    protected ApplicationResource getResourceInstance()
    {
        applicationService = mock( ApplicationService.class );

        final ApplicationResource applicationResource = new ApplicationResource( applicationService, _ -> {
        } );
        applicationResource.applicationLoader = mock( ApplicationLoader.class );
        return applicationResource;
    }

    @Test
    void install()
    {
        ApplicationResource resource = getResourceInstance();

        ByteSource byteSource = ByteSource.wrap( "bytes".getBytes() );

        Application application = createApplication();

        MultipartItem multipartItem = mock( MultipartItem.class );
        when( multipartItem.getBytes() ).thenReturn( byteSource );
        String fileName = application.getDisplayName();
        when( multipartItem.getFileName() ).thenReturn( fileName );

        MultipartForm multipartForm = mock( MultipartForm.class );

        when( this.applicationService.installGlobalApplication( any( ByteSource.class ) ) ).thenReturn( application );

        when( multipartForm.get( "file" ) ).thenReturn( multipartItem );

        ApplicationInstallResultJson result = resource.install( multipartForm );

        assertEquals( application.getKey().toString(), result.getApplicationInstalledJson().getApplication().getKey() );
        assertFalse( result.getApplicationInstalledJson().getApplication().getLocal() );
    }

    @Test
    void install_exception()
    {
        ApplicationResource resource = getResourceInstance();

        MultipartItem multipartItem = mock( MultipartItem.class );

        ByteSource byteSource = ByteSource.wrap( "bytes".getBytes() );
        String fileName = "app-name";

        when( multipartItem.getBytes() ).thenReturn( byteSource );
        when( multipartItem.getFileName() ).thenReturn( fileName );

        MultipartForm multipartForm = mock( MultipartForm.class );

        when( this.applicationService.installGlobalApplication( any( ByteSource.class ) ) ).thenThrow( new RuntimeException() );

        when( multipartForm.get( "file" ) ).thenReturn( multipartItem );

        ApplicationInstallResultJson result = resource.install( multipartForm );

        assertEquals( "Failed to process application app-name", result.getFailure() );
    }

    @Test
    void install_missing_file_item()
    {
        ApplicationResource resource = getResourceInstance();

        MultipartForm multipartForm = mock( MultipartForm.class );

        final RuntimeException ex = assertThrows( RuntimeException.class, () -> resource.install( multipartForm ) );
        assertEquals( "Missing file item", ex.getMessage() );
    }

    @Test
    void install_url()
        throws Exception
    {
        Application application = createApplication();

        when( this.applicationService.installGlobalApplication( any() ) ).thenReturn( application );

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
        Application application = createApplication();

        when( this.applicationService.installGlobalApplication( any() ) ).thenThrow( new RuntimeException() );

        String jsonString = request().path( "app/installUrl" )
            .entity( "{\"URL\":\"" + application.getUrl() + "\"}", MediaType.APPLICATION_JSON_TYPE )
            .post()
            .getAsString();

        assertEquals( "{\"failure\":\"Failed to upload application from https://enonic.net\"}", jsonString );
    }

    @Test
    void install_invalid_url()
        throws Exception
    {
        Application application = createApplication( "invalid url" );

        String jsonString = request().path( "app/installUrl" )
            .entity( "{\"URL\":\"" + application.getUrl() + "\"}", MediaType.APPLICATION_JSON_TYPE )
            .post()
            .getAsString();

        assertEquals( "{\"failure\":\"Failed to upload application from invalid url\"}", jsonString );
    }

    @Test
    void test_start_application()
        throws Exception
    {
        final Application application = createApplication();

        request().path( "app/start" ).entity( "{\"key\": \"" + application.getKey() + "\" }", MediaType.APPLICATION_JSON_TYPE ).post();

        final InOrder inOrder = Mockito.inOrder( applicationService );
        inOrder.verify( this.applicationService ).startApplication( application.getKey() );
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void test_stop_application()
        throws Exception
    {
        final Application application = createApplication();

        request().path( "app/stop" ).entity( "{\"key\": \"" + application.getKey() + "\" }", MediaType.APPLICATION_JSON_TYPE ).post();

        final InOrder inOrder = Mockito.inOrder( applicationService );
        inOrder.verify( this.applicationService ).stopApplication( application.getKey() );
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void test_uninstall_application()
        throws Exception
    {
        final Application application = createApplication();

        request().path( "app/uninstall" ).entity( "{\"key\": \"" + application.getKey() + "\" }", MediaType.APPLICATION_JSON_TYPE ).post();

        final InOrder inOrder = Mockito.inOrder( applicationService );
        inOrder.verify( this.applicationService ).uninstallApplication( application.getKey() );
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
