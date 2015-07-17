package com.enonic.xp.admin.impl.rest.resource.module;

import java.time.Instant;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Version;

import com.enonic.xp.admin.impl.rest.resource.AbstractResourceTest;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.inputtype.InputTypes;
import com.enonic.xp.module.Module;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;

public class ModuleResourceTest
    extends AbstractResourceTest
{
    private ApplicationService applicationService;

    private SiteService siteService;

    @Test
    public void get_module_list()
        throws Exception
    {
        final Module module = createModule();
        final Applications applications = Applications.from( module );
        Mockito.when( this.applicationService.getAllModules() ).thenReturn( applications );
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
        final Module module = createModule();
        final Applications applications = Applications.from( module, createEmptyModule() );
        Mockito.when( this.applicationService.getAllModules() ).thenReturn( applications );
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
        final Module module = createModule();
        final Applications applications = Applications.from( module, createEmptyModule() );
        Mockito.when( this.applicationService.getAllModules() ).thenReturn( applications );
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
        final Module module = createModule();
        Mockito.when( this.applicationService.getModule( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( module );
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

        Mockito.verify( this.applicationService ).startModule( ApplicationKey.from( "testmodule" ) );
    }

    @Test
    public void stop_module()
        throws Exception
    {
        request().
            path( "module/stop" ).
            entity( "{\"key\":[\"testmodule\"]}", MediaType.APPLICATION_JSON_TYPE ).
            post();

        Mockito.verify( this.applicationService ).stopModule( ApplicationKey.from( "testmodule" ) );
    }

    private Module createModule()
    {
        final Module module = Mockito.mock( Module.class );
        Mockito.when( module.getKey() ).thenReturn( ApplicationKey.from( "testmodule" ) );
        Mockito.when( module.getVersion() ).thenReturn( new Version( 1, 0, 0 ) );
        Mockito.when( module.getDisplayName() ).thenReturn( "module display name" );
        Mockito.when( module.getUrl() ).thenReturn( "http://enonic.net" );
        Mockito.when( module.getVendorName() ).thenReturn( "Enonic" );
        Mockito.when( module.getVendorUrl() ).thenReturn( "https://www.enonic.com" );
        Mockito.when( module.getMinSystemVersion() ).thenReturn( "5.0" );
        Mockito.when( module.getMaxSystemVersion() ).thenReturn( "5.1" );
        Mockito.when( module.isStarted() ).thenReturn( true );
        Mockito.when( module.getModifiedTime() ).thenReturn( Instant.parse( "2012-01-01T00:00:00.00Z" ) );

        return module;
    }

    private Module createEmptyModule()
    {
        final Module module = Mockito.mock( Module.class );
        Mockito.when( module.getKey() ).thenReturn( ApplicationKey.from( "empty_testmodule" ) );
        Mockito.when( module.getDisplayName() ).thenReturn( "empty name" );

        return module;
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
