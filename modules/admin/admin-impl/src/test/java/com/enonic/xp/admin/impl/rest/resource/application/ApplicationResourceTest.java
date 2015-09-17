package com.enonic.xp.admin.impl.rest.resource.application;

import java.time.Instant;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Version;

import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;

public class ApplicationResourceTest
    extends AdminResourceTestSupport
{
    private ApplicationService applicationService;

    private SiteService siteService;

    @Test
    public void get_application_list()
        throws Exception
    {
        final Application application = createApplication();
        final Applications applications = Applications.from( application );
        Mockito.when( this.applicationService.getAllApplications() ).thenReturn( applications );
        final SiteDescriptor siteDescriptor = createSiteDescriptor();
        Mockito.when( this.siteService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( siteDescriptor );

        String response = request().
            path( "application/list" ).
            get().getAsString();
        assertJson( "get_application_list_success.json", response );
    }

    @Test
    public void get_application_list_with_query()
        throws Exception
    {
        final Application application = createApplication();
        final Applications applications = Applications.from( application, createEmptyApplication() );
        Mockito.when( this.applicationService.getAllApplications() ).thenReturn( applications );
        final SiteDescriptor siteDescriptor = createSiteDescriptor();
        Mockito.when( this.siteService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( siteDescriptor );

        String response = request().
            path( "application/list" ).
            queryParam( "query", "Enonic" ).
            get().getAsString();
        assertJson( "get_application_list_success.json", response );
    }

    @Test
    public void get_application_list_with_invalid_query()
        throws Exception
    {
        final Application application = createApplication();
        final Applications applications = Applications.from( application, createEmptyApplication() );
        Mockito.when( this.applicationService.getAllApplications() ).thenReturn( applications );
        final SiteDescriptor siteDescriptor = createSiteDescriptor();
        Mockito.when( this.siteService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( siteDescriptor );

        String response = request().
            path( "application/list" ).
            queryParam( "query", "invalid query" ).
            get().getAsString();
        assertJson( "get_application_list_with_invalid_query.json", response );
    }

    @Test
    public void get_application_by_key()
        throws Exception
    {
        final Application application = createApplication();
        Mockito.when( this.applicationService.getApplication( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( application );
        final SiteDescriptor siteDescriptor = createSiteDescriptor();
        Mockito.when( this.siteService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( siteDescriptor );

        String response = request().
            path( "application" ).
            queryParam( "applicationKey", "testapplication" ).
            get().getAsString();
        assertJson( "get_application_by_key_success.json", response );
    }

    @Test
    public void start_application()
        throws Exception
    {
        request().
            path( "application/start" ).
            entity( "{\"key\":[\"testapplication\"]}", MediaType.APPLICATION_JSON_TYPE ).
            post();

        Mockito.verify( this.applicationService ).startApplication( ApplicationKey.from( "testapplication" ) );
    }

    @Test
    public void stop_application()
        throws Exception
    {
        request().
            path( "application/stop" ).
            entity( "{\"key\":[\"testapplication\"]}", MediaType.APPLICATION_JSON_TYPE ).
            post();

        Mockito.verify( this.applicationService ).stopApplication( ApplicationKey.from( "testapplication" ) );
    }

    private Application createApplication()
    {
        final Application application = Mockito.mock( Application.class );
        Mockito.when( application.getKey() ).thenReturn( ApplicationKey.from( "testapplication" ) );
        Mockito.when( application.getVersion() ).thenReturn( new Version( 1, 0, 0 ) );
        Mockito.when( application.getDisplayName() ).thenReturn( "application display name" );
        Mockito.when( application.getUrl() ).thenReturn( "http://enonic.net" );
        Mockito.when( application.getVendorName() ).thenReturn( "Enonic" );
        Mockito.when( application.getVendorUrl() ).thenReturn( "https://www.enonic.com" );
        Mockito.when( application.getMinSystemVersion() ).thenReturn( "5.0" );
        Mockito.when( application.getMaxSystemVersion() ).thenReturn( "5.1" );
        Mockito.when( application.isStarted() ).thenReturn( true );
        Mockito.when( application.getModifiedTime() ).thenReturn( Instant.parse( "2012-01-01T00:00:00.00Z" ) );

        return application;
    }

    private Application createEmptyApplication()
    {
        final Application application = Mockito.mock( Application.class );
        Mockito.when( application.getKey() ).thenReturn( ApplicationKey.from( "empty_testapplication" ) );
        Mockito.when( application.getDisplayName() ).thenReturn( "empty name" );

        return application;
    }

    private SiteDescriptor createSiteDescriptor()
    {
        final Form config = Form.create().
            addFormItem( Input.create().name( "some-name" ).label( "some-label" ).inputType( InputTypeName.TEXT_LINE ).build() ).
            build();
        return SiteDescriptor.create().form( config ).build();
    }

    @Override
    protected Object getResourceInstance()
    {
        this.applicationService = Mockito.mock( ApplicationService.class );
        this.siteService = Mockito.mock( SiteService.class );

        final ApplicationResource resource = new ApplicationResource();
        resource.setApplicationService( this.applicationService );
        resource.setSiteService( this.siteService );

        return resource;
    }
}
