package com.enonic.xp.admin.impl.rest.resource.module;

import java.time.Instant;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

import com.enonic.xp.admin.impl.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.module.ModuleVersion;
import com.enonic.wem.api.module.Modules;

public class ModuleResourceTest
    extends AbstractResourceTest
{
    private ModuleService moduleService;

    private BundleContext bundleContext;

    @Test
    public void get_module_list()
        throws Exception
    {
        final Module module = createModule();
        final Modules modules = Modules.from( module );
        Mockito.when( this.moduleService.getAllModules() ).thenReturn( modules );

        String response = request().
            path( "module/list" ).
            get().getAsString();
        assertJson( "get_module_list_success.json", response );
    }

    @Test
    public void get_module_by_key()
        throws Exception
    {
        final Module module = createModule();
        Mockito.when( this.moduleService.getModule( Mockito.isA( ModuleKey.class ) ) ).thenReturn( module );

        String response = request().
            path( "module" ).
            queryParam( "moduleKey", "testmodule" ).
            get().getAsString();
        assertJson( "get_module_by_key_success.json", response );
    }

    @Test
    public void start_module()
        throws Exception
    {
        final Module module = createModule();
        Mockito.when( this.moduleService.getModule( Mockito.isA( ModuleKey.class ) ) ).thenReturn( module );

        request().
            path( "module/start" ).
            entity( "{\"key\":[\"testmodule\"]}", MediaType.APPLICATION_JSON_TYPE ).
            post();

        Mockito.verify( module.getBundle() ).start();
    }

    @Test
    public void stop_module()
        throws Exception
    {
        final Module module = createModule();
        Mockito.when( this.moduleService.getModule( Mockito.isA( ModuleKey.class ) ) ).thenReturn( module );

        request().
            path( "module/stop" ).
            entity( "{\"key\":[\"testmodule\"]}", MediaType.APPLICATION_JSON_TYPE ).
            post();

        Mockito.verify( module.getBundle() ).stop();
    }

    @Test
    public void update_module()
        throws Exception
    {
        final Module module = createModule();
        Mockito.when( this.moduleService.getModule( Mockito.isA( ModuleKey.class ) ) ).thenReturn( module );

        request().
            path( "module/update" ).
            entity( "{\"key\":[\"testmodule\"]}", MediaType.APPLICATION_JSON_TYPE ).
            post();

        Mockito.verify( module.getBundle() ).update();
    }

    @Test
    public void uninstall_module()
        throws Exception
    {
        final Module module = createModule();
        Mockito.when( this.moduleService.getModule( Mockito.isA( ModuleKey.class ) ) ).thenReturn( module );

        request().
            path( "module/uninstall" ).
            entity( "{\"key\":[\"testmodule\"]}", MediaType.APPLICATION_JSON_TYPE ).
            post();

        Mockito.verify( module.getBundle() ).uninstall();
    }

    @Test
    public void install_module()
        throws Exception
    {
        request().
            path( "module/install" ).
            entity( "{\"url\":\"http://some.host/some.path\"}", MediaType.APPLICATION_JSON_TYPE ).
            post();

        Mockito.verify( this.bundleContext ).installBundle( "http://some.host/some.path" );
    }

    private Module createModule()
    {
        final Form config = Form.newForm().
            addFormItem( Input.newInput().name( "some-name" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();

        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getState() ).thenReturn( Bundle.ACTIVE );
        Mockito.when( bundle.getLastModified() ).thenReturn( Instant.parse( "2012-01-01T00:00:00.00Z" ).toEpochMilli() );

        final Module module = Mockito.mock( Module.class );
        Mockito.when( module.getKey() ).thenReturn( ModuleKey.from( "testmodule" ) );
        Mockito.when( module.getVersion() ).thenReturn( ModuleVersion.from( "1.0.0" ) );
        Mockito.when( module.getDisplayName() ).thenReturn( "module display name" );
        Mockito.when( module.getUrl() ).thenReturn( "http://enonic.net" );
        Mockito.when( module.getVendorName() ).thenReturn( "Enonic" );
        Mockito.when( module.getVendorUrl() ).thenReturn( "https://www.enonic.com" );
        Mockito.when( module.getMinSystemVersion() ).thenReturn( "5.0" );
        Mockito.when( module.getMaxSystemVersion() ).thenReturn( "5.1" );
        Mockito.when( module.getBundle() ).thenReturn( bundle );
        Mockito.when( module.getConfig() ).thenReturn( config );

        return module;
    }

    @Override
    protected Object getResourceInstance()
    {
        this.moduleService = Mockito.mock( ModuleService.class );
        this.bundleContext = Mockito.mock( BundleContext.class );

        final ModuleResource resource = new ModuleResource();

        final ComponentContext componentContext = Mockito.mock( ComponentContext.class );
        Mockito.when( componentContext.getBundleContext() ).thenReturn( this.bundleContext );

        resource.setModuleService( this.moduleService );
        resource.initialize( componentContext );

        return resource;
    }
}
