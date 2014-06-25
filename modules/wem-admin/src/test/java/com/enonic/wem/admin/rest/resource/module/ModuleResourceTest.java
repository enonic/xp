package com.enonic.wem.admin.rest.resource.module;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.module.Modules;
import com.enonic.wem.core.module.ModuleBuilder;

public class ModuleResourceTest
    extends AbstractResourceTest
{
    private ModuleService moduleService;

    private Path tempDir;

    private BundleContext bundleContext;

    @Before
    public void setup()
        throws IOException
    {
        mockCurrentContextHttpRequest();
        tempDir = Files.createTempDirectory( "wemtest" );
    }

    @After
    public void after()
    {
        try
        {
            if ( tempDir != null )
            {
                FileUtils.deleteDirectory( tempDir.toFile() );
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    @Test
    public void get_module_list()
        throws Exception
    {
        final Module module = createModule();
        Mockito.when( this.moduleService.getAllModules() ).thenReturn( Modules.from( module ) );

        String response = resource().
            path( "module/list" ).
            get( String.class );
        assertJson( "get_module_list_success.json", response );
    }

    @Test
    public void get_module_by_key()
        throws Exception
    {
        final Module module = createModule();
        Mockito.when( this.moduleService.getModule( Mockito.isA( ModuleKey.class ) ) ).thenReturn( module );

        String response = resource().
            path( "module" ).
            queryParam( "moduleKey", "testmodule-1.0.0" ).
            get( String.class );
        assertJson( "get_module_by_key_success.json", response );
    }

    @Test
    public void start_module()
        throws Exception
    {
        final Module module = createModule();
        Mockito.when( this.moduleService.getModule( Mockito.isA( ModuleKey.class ) ) ).thenReturn( module );

        resource().
            path( "module/start" ).
            type( MediaType.APPLICATION_JSON_TYPE ).
            post( "{\"key\":[\"testmodule-1.0.0\"]}" );

        Mockito.verify( module.getBundle() ).start();
    }

    @Test
    public void stop_module()
        throws Exception
    {
        final Module module = createModule();
        Mockito.when( this.moduleService.getModule( Mockito.isA( ModuleKey.class ) ) ).thenReturn( module );

        resource().
            path( "module/stop" ).
            type( MediaType.APPLICATION_JSON_TYPE ).
            post( "{\"key\":[\"testmodule-1.0.0\"]}" );

        Mockito.verify( module.getBundle() ).stop();
    }

    @Test
    public void update_module()
        throws Exception
    {
        final Module module = createModule();
        Mockito.when( this.moduleService.getModule( Mockito.isA( ModuleKey.class ) ) ).thenReturn( module );

        resource().
            path( "module/update" ).
            type( MediaType.APPLICATION_JSON_TYPE ).
            post( "{\"key\":[\"testmodule-1.0.0\"]}" );

        Mockito.verify( module.getBundle() ).update();
    }

    @Test
    public void uninstall_module()
        throws Exception
    {
        final Module module = createModule();
        Mockito.when( this.moduleService.getModule( Mockito.isA( ModuleKey.class ) ) ).thenReturn( module );

        resource().
            path( "module/uninstall" ).
            type( MediaType.APPLICATION_JSON_TYPE ).
            post( "{\"key\":[\"testmodule-1.0.0\"]}" );

        Mockito.verify( module.getBundle() ).uninstall();
    }

    @Test
    public void install_module()
        throws Exception
    {
        resource().
            path( "module/install" ).
            type( MediaType.APPLICATION_JSON_TYPE ).
            post( "{\"url\":\"http://some.host/some.path\"}" );

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
            moduleKey( ModuleKey.from( "testmodule-1.0.0" ) ).
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
        resource.moduleService = this.moduleService;
        resource.bundleContext = this.bundleContext;

        return resource;
    }

}
