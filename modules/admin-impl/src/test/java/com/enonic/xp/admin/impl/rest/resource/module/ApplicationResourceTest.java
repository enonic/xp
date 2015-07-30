package com.enonic.xp.admin.impl.rest.resource.module;

import java.time.Instant;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Version;

import com.enonic.xp.admin.impl.rest.resource.AbstractResourceTest;
import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.inputtype.InputTypes;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;

public class ApplicationResourceTest
    extends AbstractResourceTest
{
    private ApplicationService applicationService;

    private SiteService siteService;

    @Test
    public void get_module_list()
        throws Exception
    {
        final Application application = createModule();
        final Applications applications = Applications.from( application );
        Mockito.when( this.applicationService.getAllApplications() ).thenReturn( applications );
        final SiteDescriptor siteDescriptor = createSiteDescriptor();
        Mockito.when( this.siteService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( siteDescriptor );

        String response = request().
            path( "module/list" ).
            get().getAsString();
        assertJson( "get_module_list_success.json", response );
    }

    @Test
    public void get_module_list_with_query()
        throws Exception
    {
        final Application application = createModule();
        final Applications applications = Applications.from( application, createEmptyModule() );
        Mockito.when( this.applicationService.getAllApplications() ).thenReturn( applications );
        final SiteDescriptor siteDescriptor = createSiteDescriptor();
        Mockito.when( this.siteService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( siteDescriptor );

        String response = request().
            path( "module/list" ).
            queryParam( "query", "Enonic" ).
            get().getAsString();
        assertJson( "get_module_list_success.json", response );
    }

    @Test
    public void get_module_list_with_invalid_query()
        throws Exception
    {
        final Application application = createModule();
        final Applications applications = Applications.from( application, createEmptyModule() );
        Mockito.when( this.applicationService.getAllApplications() ).thenReturn( applications );
        final SiteDescriptor siteDescriptor = createSiteDescriptor();
        Mockito.when( this.siteService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( siteDescriptor );

        String response = request().
            path( "module/list" ).
            queryParam( "query", "invalid query" ).
            get().getAsString();
        assertJson( "get_module_list_with_invalid_query.json", response );
    }

    @Test
    public void get_module_by_key()
        throws Exception
    {
        final Application application = createModule();
        Mockito.when( this.applicationService.getApplication( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( application );
        final SiteDescriptor siteDescriptor = createSiteDescriptor();
        Mockito.when( this.siteService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( siteDescriptor );

        String response = request().
            path( "module" ).
            queryParam( "applicationKey", "testmodule" ).
            get().getAsString();
        assertJson( "get_module_by_key_success.json", response );
    }

    @Test
    public void start_module()
        throws Exception
    {
        request().
            path( "module/start" ).
            entity( "{\"key\":[\"testmodule\"]}", MediaType.APPLICATION_JSON_TYPE ).
            post();

        Mockito.verify( this.applicationService ).startApplication( ApplicationKey.from( "testmodule" ) );
    }

    @Test
    public void stop_module()
        throws Exception
    {
        request().
            path( "module/stop" ).
            entity( "{\"key\":[\"testmodule\"]}", MediaType.APPLICATION_JSON_TYPE ).
            post();

        Mockito.verify( this.applicationService ).stopApplication( ApplicationKey.from( "testmodule" ) );
    }

    private Application createModule()
    {
        final Application application = Mockito.mock( Application.class );
        Mockito.when( application.getKey() ).thenReturn( ApplicationKey.from( "testmodule" ) );
        Mockito.when( application.getVersion() ).thenReturn( new Version( 1, 0, 0 ) );
        Mockito.when( application.getDisplayName() ).thenReturn( "module display name" );
        Mockito.when( application.getUrl() ).thenReturn( "http://enonic.net" );
        Mockito.when( application.getVendorName() ).thenReturn( "Enonic" );
        Mockito.when( application.getVendorUrl() ).thenReturn( "https://www.enonic.com" );
        Mockito.when( application.getMinSystemVersion() ).thenReturn( "5.0" );
        Mockito.when( application.getMaxSystemVersion() ).thenReturn( "5.1" );
        Mockito.when( application.isStarted() ).thenReturn( true );
        Mockito.when( application.getModifiedTime() ).thenReturn( Instant.parse( "2012-01-01T00:00:00.00Z" ) );

        return application;
    }

    private Application createEmptyModule()
    {
        final Application application = Mockito.mock( Application.class );
        Mockito.when( application.getKey() ).thenReturn( ApplicationKey.from( "empty_testmodule" ) );
        Mockito.when( application.getDisplayName() ).thenReturn( "empty name" );

        return application;
    }

    private SiteDescriptor createSiteDescriptor()
    {
        final Form config = Form.create().
            addFormItem( Input.create().name( "some-name" ).label( "some-label" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();
        return SiteDescriptor.create().form( config ).build();
    }

    @Override
    protected Object getResourceInstance()
    {
        this.applicationService = Mockito.mock( ApplicationService.class );
        this.siteService = Mockito.mock( SiteService.class );

        final ModuleResource resource = new ModuleResource();
        resource.setApplicationService( this.applicationService );
        resource.setSiteService( this.siteService );

        return resource;
    }
}
