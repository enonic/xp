package com.enonic.wem.admin.rest.resource.module;

import java.time.Instant;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleBuilder;
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
        Mockito.when( this.moduleService.getAllModules() ).thenReturn( Modules.from( module ) );

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

        return new ModuleBuilder().
            moduleKey( ModuleKey.from( "testmodule" ) ).
            moduleVersion( ModuleVersion.from( "1.0.0" ) ).
            displayName( "module display name" ).
            url( "http://enonic.net" ).
            vendorName( "Enonic" ).
            vendorUrl( "https://www.enonic.com" ).
            config( config ).
            bundle( bundle ).
            build();
    }

    @Override
    protected Object getResourceInstance()
    {
        this.moduleService = Mockito.mock( ModuleService.class );
        this.bundleContext = Mockito.mock( BundleContext.class );

        final ModuleResource resource = new ModuleResource();
        resource.setModuleService( this.moduleService );
        resource.setBundleContext( this.bundleContext );

        return resource;
    }
}
