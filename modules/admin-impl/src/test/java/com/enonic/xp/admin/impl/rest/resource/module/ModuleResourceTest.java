package com.enonic.xp.admin.impl.rest.resource.module;

import java.time.Instant;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.impl.rest.resource.AbstractResourceTest;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.inputtype.InputTypes;
import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleService;
import com.enonic.xp.module.ModuleVersion;
import com.enonic.xp.module.Modules;

public class ModuleResourceTest
    extends AbstractResourceTest
{
    private ModuleService moduleService;

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
        request().
            path( "module/start" ).
            entity( "{\"key\":[\"testmodule\"]}", MediaType.APPLICATION_JSON_TYPE ).
            post();

        Mockito.verify( this.moduleService ).startModule( ModuleKey.from( "testmodule" ) );
    }

    @Test
    public void stop_module()
        throws Exception
    {
        request().
            path( "module/stop" ).
            entity( "{\"key\":[\"testmodule\"]}", MediaType.APPLICATION_JSON_TYPE ).
            post();

        Mockito.verify( this.moduleService ).stopModule( ModuleKey.from( "testmodule" ) );
    }

    @Test
    public void update_module()
        throws Exception
    {
        request().
            path( "module/update" ).
            entity( "{\"key\":[\"testmodule\"]}", MediaType.APPLICATION_JSON_TYPE ).
            post();

        Mockito.verify( this.moduleService ).updateModule( ModuleKey.from( "testmodule" ) );
    }

    @Test
    public void uninstall_module()
        throws Exception
    {
        request().
            path( "module/uninstall" ).
            entity( "{\"key\":[\"testmodule\"]}", MediaType.APPLICATION_JSON_TYPE ).
            post();

        Mockito.verify( this.moduleService ).uninstallModule( ModuleKey.from( "testmodule" ) );
    }

    @Test
    public void install_module()
        throws Exception
    {
        request().
            path( "module/install" ).
            entity( "{\"url\":\"http://some.host/some.path\"}", MediaType.APPLICATION_JSON_TYPE ).
            post();

        Mockito.verify( this.moduleService ).installModule( "http://some.host/some.path" );
    }

    private Module createModule()
    {
        final Form config = Form.newForm().
            addFormItem( Input.create().name( "some-name" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();

        final Module module = Mockito.mock( Module.class );
        Mockito.when( module.getKey() ).thenReturn( ModuleKey.from( "testmodule" ) );
        Mockito.when( module.getVersion() ).thenReturn( ModuleVersion.from( "1.0.0" ) );
        Mockito.when( module.getDisplayName() ).thenReturn( "module display name" );
        Mockito.when( module.getUrl() ).thenReturn( "http://enonic.net" );
        Mockito.when( module.getVendorName() ).thenReturn( "Enonic" );
        Mockito.when( module.getVendorUrl() ).thenReturn( "https://www.enonic.com" );
        Mockito.when( module.getMinSystemVersion() ).thenReturn( "5.0" );
        Mockito.when( module.getMaxSystemVersion() ).thenReturn( "5.1" );
        Mockito.when( module.isStarted() ).thenReturn( true );
        Mockito.when( module.getModifiedTime() ).thenReturn( Instant.parse( "2012-01-01T00:00:00.00Z" ) );
        Mockito.when( module.getConfig() ).thenReturn( config );

        return module;
    }

    @Override
    protected Object getResourceInstance()
    {
        this.moduleService = Mockito.mock( ModuleService.class );

        final ModuleResource resource = new ModuleResource();
        resource.setModuleService( this.moduleService );

        return resource;
    }
}
