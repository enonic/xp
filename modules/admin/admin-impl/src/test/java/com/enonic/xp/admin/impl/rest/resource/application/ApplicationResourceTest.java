package com.enonic.xp.admin.impl.rest.resource.application;

import java.time.Instant;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Version;

import com.enonic.xp.admin.impl.market.MarketService;
import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.auth.AuthDescriptor;
import com.enonic.xp.auth.AuthDescriptorService;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;

public class ApplicationResourceTest
    extends AdminResourceTestSupport
{
    private ApplicationService applicationService;

    private ApplicationDescriptorService applicationDescriptorService;

    private SiteService siteService;

    private MarketService marketService;

    private AuthDescriptorService authDescriptorService;

    @Test
    public void get_application_list()
        throws Exception
    {
        final Application application = createApplication();
        final Applications applications = Applications.from( application );
        Mockito.when( this.applicationService.getInstalledApplications() ).thenReturn( applications );
        final SiteDescriptor siteDescriptor = createSiteDescriptor();
        Mockito.when( this.siteService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( siteDescriptor );
        final AuthDescriptor authDescriptor = createAuthDescriptor();
        Mockito.when( this.authDescriptorService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( authDescriptor );
        final ApplicationDescriptor appDescriptor = createApplicationDescriptor();
        Mockito.when( this.applicationDescriptorService.get( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( appDescriptor );

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
        Mockito.when( this.applicationService.getInstalledApplications() ).thenReturn( applications );
        final SiteDescriptor siteDescriptor = createSiteDescriptor();
        Mockito.when( this.siteService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( siteDescriptor );
        final AuthDescriptor authDescriptor = createAuthDescriptor();
        Mockito.when( this.authDescriptorService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( authDescriptor );
        final ApplicationDescriptor appDescriptor = createApplicationDescriptor();
        Mockito.when( this.applicationDescriptorService.get( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( appDescriptor );

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
        Mockito.when( this.applicationService.getInstalledApplications() ).thenReturn( applications );
        final SiteDescriptor siteDescriptor = createSiteDescriptor();
        Mockito.when( this.siteService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( siteDescriptor );
        final AuthDescriptor authDescriptor = createAuthDescriptor();
        Mockito.when( this.authDescriptorService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( authDescriptor );
        final ApplicationDescriptor appDescriptor = createApplicationDescriptor();
        Mockito.when( this.applicationDescriptorService.get( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( appDescriptor );

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
        Mockito.when( this.applicationService.getInstalledApplication( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( application );
        final SiteDescriptor siteDescriptor = createSiteDescriptor();
        Mockito.when( this.siteService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( siteDescriptor );
        final AuthDescriptor authDescriptor = createAuthDescriptor();
        Mockito.when( this.authDescriptorService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( authDescriptor );
        final ApplicationDescriptor appDescriptor = createApplicationDescriptor();
        Mockito.when( this.applicationDescriptorService.get( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( appDescriptor );

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

        Mockito.verify( this.applicationService ).startApplication( ApplicationKey.from( "testapplication" ), true );
    }

    @Test
    public void stop_application()
        throws Exception
    {
        request().
            path( "application/stop" ).
            entity( "{\"key\":[\"testapplication\"]}", MediaType.APPLICATION_JSON_TYPE ).
            post();

        Mockito.verify( this.applicationService ).stopApplication( ApplicationKey.from( "testapplication" ), true );
    }

    @Test
    public void get_id_provider_applications()
        throws Exception
    {
        final Application application = createApplication();
        final Applications applications = Applications.from( application );
        Mockito.when( this.applicationService.getInstalledApplications() ).thenReturn( applications );
        final AuthDescriptor authDescriptor = createAuthDescriptor();
        Mockito.when( this.authDescriptorService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( authDescriptor );
        final ApplicationDescriptor appDescriptor = createApplicationDescriptor();
        Mockito.when( this.applicationDescriptorService.get( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( appDescriptor );

        String response = request().
            path( "application/getIdProviderApplications" ).
            get().getAsString();
        assertJson( "get_id_provider_applications.json", response );
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
        Mockito.when( application.hasSiteDescriptor() ).thenReturn( true );

        return application;
    }

    private ApplicationDescriptor createApplicationDescriptor()
    {
        return ApplicationDescriptor.create().
            key( ApplicationKey.from( "testapplication" ) ).
            description( "Application description" ).
            build();
    }

    private Application createEmptyApplication()
    {
        final Application application = Mockito.mock( Application.class );
        Mockito.when( application.getDisplayName() ).thenReturn( "empty name" );
        Mockito.when( application.getKey() ).thenReturn( ApplicationKey.from( "empty_testapplication" ) );
        Mockito.when( application.hasSiteDescriptor() ).thenReturn( true );
        return application;
    }

    private SiteDescriptor createSiteDescriptor()
    {
        final Form config = Form.create().
            addFormItem( Input.create().name( "some-name" ).label( "some-label" ).inputType( InputTypeName.TEXT_LINE ).build() ).
            build();
        return SiteDescriptor.create().form( config ).build();
    }

    private AuthDescriptor createAuthDescriptor()
    {
        final Form config = Form.create().
            addFormItem( Input.create().name( "some-name" ).label( "some-label" ).inputType( InputTypeName.TEXT_LINE ).build() ).
            build();
        return AuthDescriptor.create().
            config( config ).
            build();
    }

    @Override
    protected Object getResourceInstance()
    {
        this.applicationService = Mockito.mock( ApplicationService.class );
        this.applicationDescriptorService = Mockito.mock( ApplicationDescriptorService.class );
        this.siteService = Mockito.mock( SiteService.class );
        this.marketService = Mockito.mock( MarketService.class );
        this.authDescriptorService = Mockito.mock( AuthDescriptorService.class );

        final ApplicationResource resource = new ApplicationResource();
        resource.setApplicationService( this.applicationService );
        resource.setSiteService( this.siteService );
        resource.setMarketService( this.marketService );
        resource.setAuthDescriptorService( this.authDescriptorService );
        resource.setApplicationDescriptorService( this.applicationDescriptorService );

        return resource;
    }
}
