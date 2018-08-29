package com.enonic.xp.impl.server.rest;

import java.net.URL;
import java.time.Instant;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.osgi.framework.Version;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.impl.server.rest.model.ApplicationInstallResultJson;
import com.enonic.xp.impl.server.rest.model.ApplicationInstalledJson;

import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartItem;

import static org.mockito.Matchers.eq;

public class ApplicationResourceTest
    extends ServerRestTestSupport
{
    private ApplicationService applicationService;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void install()
        throws Exception
    {

        ApplicationResource resource = getResourceInstance();

        ByteSource byteSource = ByteSource.wrap( "bytes".getBytes() );

        Application application = createApplication();

        MultipartItem multipartItem = Mockito.mock( MultipartItem.class );
        Mockito.when( multipartItem.getBytes() ).thenReturn( byteSource );
        String fileName = application.getDisplayName();
        Mockito.when( multipartItem.getFileName() ).thenReturn( fileName );

        MultipartForm multipartForm = Mockito.mock( MultipartForm.class );

        Mockito.when( this.applicationService.installGlobalApplication( Mockito.isA( ByteSource.class ), eq( fileName ) ) ).thenReturn(
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

        MultipartItem multipartItem = Mockito.mock( MultipartItem.class );

        ByteSource byteSource = ByteSource.wrap( "bytes".getBytes() );
        String fileName = "app-name";

        Mockito.when( multipartItem.getBytes() ).thenReturn( byteSource );
        Mockito.when( multipartItem.getFileName() ).thenReturn( fileName );

        MultipartForm multipartForm = Mockito.mock( MultipartForm.class );

        Mockito.when( this.applicationService.installGlobalApplication( Mockito.isA( ByteSource.class ), eq( "app-name" ) ) ).thenThrow(
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

        MultipartForm multipartForm = Mockito.mock( MultipartForm.class );

        expectedEx.expect( RuntimeException.class );
        expectedEx.expectMessage( "Missing file item" );

        resource.install( multipartForm );
    }

    @Override
    protected ApplicationResource getResourceInstance()
    {
        applicationService = Mockito.mock( ApplicationService.class );

        final ApplicationResource resource = new ApplicationResource();
        resource.setApplicationService( applicationService );

        return resource;
    }

    @Test
    public void install_url()
        throws Exception
    {
        Application application = createApplication();

        Mockito.when( this.applicationService.installGlobalApplication( new URL( application.getUrl() ) ) ).thenReturn( application );

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

        assertEquals( "{\"applicationInstalledJson\":null,\"failure\":\"Illegal protocol: ftp\"}", jsonString );
    }

    @Test
    public void install_url_process_error()
        throws Exception
    {
        Application application = createApplication();

        Mockito.when( this.applicationService.installGlobalApplication( new URL( application.getUrl() ) ) ).thenThrow(
            new RuntimeException() );

        String jsonString = request().path( "app/installUrl" ).
            entity( "{\"URL\":\""+application.getUrl()+"\"}", MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertEquals( "{\"applicationInstalledJson\":null,\"failure\":\"Failed to process application from http://enonic.net\"}",
                      jsonString );
    }

    @Test
    public void install_invalid_url()
        throws Exception
    {
        Application application = createApplication("invalid url");

        String jsonString = request().path( "app/installUrl" ).
            entity( "{\"URL\":\""+application.getUrl()+"\"}", MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertEquals( "{\"applicationInstalledJson\":null,\"failure\":\"Failed to upload application from invalid url\"}",
                      jsonString );
    }

    private Application createApplication()
    {
        return createApplication( "http://enonic.net" );
    }

    private Application createApplication( final String url )
    {
        final Application application = Mockito.mock( Application.class );
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
