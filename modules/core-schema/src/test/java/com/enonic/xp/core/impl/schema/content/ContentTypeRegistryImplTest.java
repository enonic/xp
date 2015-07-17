package com.enonic.xp.core.impl.schema.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.service.component.ComponentContext;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.AbstractBundleTest;
import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleService;
import com.enonic.xp.module.Modules;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypes;

import static org.junit.Assert.*;

public class ContentTypeRegistryImplTest
    extends AbstractBundleTest
{

    private Bundle myBundle;

    private ApplicationKey myApplicationKey;

    private ContentType myContentType;

    private Module myModule;

    private ModuleService moduleService;

    private ContentTypeRegistryImpl service;

    @Before
    public void setups()
        throws Exception
    {
        super.setup();

        //Mocks a module
        startBundles( newBundle( "module2" ) );
        myBundle = findBundle( "module2" );
        myApplicationKey = ApplicationKey.from( myBundle );
        this.myContentType = createContentType( "module2:myContentType", "myContentType display name" );
        myModule = Mockito.mock( Module.class );
        Mockito.when( myModule.getKey() ).thenReturn( myApplicationKey );
        Mockito.when( myModule.getBundle() ).thenReturn( myBundle );

        //Mocks the module service
        moduleService = Mockito.mock( ModuleService.class );

        //Mocks the ComponentContext
        final ComponentContext componentContext = Mockito.mock( ComponentContext.class );
        Mockito.when( componentContext.getBundleContext() ).thenReturn( this.serviceRegistry.getBundleContext() );

        //Creates the service to test
        service = new ContentTypeRegistryImpl();
        service.setModuleService( moduleService );

        //Starts the service
        service.start( componentContext );
    }

    @Test
    public void test_empty()
    {
        Mockito.when( moduleService.getAllModules() ).thenReturn( Modules.empty() );

        ContentTypes contentTypes = service.getAll();
        assertNotNull( contentTypes );
        assertTrue( contentTypes.getSize() > 20 );

        contentTypes = service.getByModule( myApplicationKey );
        assertNotNull( contentTypes );
        assertEquals( 0, contentTypes.getSize() );

        ContentType contentType = service.get( this.myContentType.getName() );
        assertEquals( null, contentType );
    }

    @Test
    public void test_add_removal_module()
    {

        Modules modules = Modules.from( myModule );
        Mockito.when( moduleService.getAllModules() ).thenReturn( modules );
        Mockito.when( moduleService.getModule( myApplicationKey ) ).thenReturn( myModule );

        ContentTypes contentTypes = service.getAll();
        assertNotNull( contentTypes );
        assertTrue( contentTypes.getSize() > 20 );

        contentTypes = service.getByModule( myApplicationKey );
        assertNotNull( contentTypes );
        assertEquals( 1, contentTypes.getSize() );

        ContentType contentType = service.get( this.myContentType.getName() );
        assertNotNull( contentType );

        Mockito.when( moduleService.getAllModules() ).thenReturn( Modules.empty() );
        Mockito.when( moduleService.getModule( myApplicationKey ) ).thenReturn( null );
        service.bundleChanged( new BundleEvent( BundleEvent.UNINSTALLED, myBundle ) );

        test_empty();
    }

    @Test
    public void test_get_system_module()
    {

        Mockito.when( moduleService.getAllModules() ).thenReturn( Modules.empty() );

        ContentTypes contentTypes = service.getAll();
        assertNotNull( contentTypes );
        assertTrue( contentTypes.getSize() > 20 );

        ContentType contentType = service.get( ContentTypeName.folder() );
        assertNotNull( contentType );

        contentTypes = service.getByModule( ApplicationKey.BASE );
        assertNotNull( contentTypes );
        assertEquals( contentTypes.getSize(), 5 );

        contentTypes = service.getByModule( ApplicationKey.PORTAL );
        assertNotNull( contentTypes );
        assertEquals( contentTypes.getSize(), 3 );

        contentTypes = service.getByModule( ApplicationKey.MEDIA_MOD );
        assertNotNull( contentTypes );
        assertEquals( contentTypes.getSize(), 13 );

        contentType = service.get( ContentTypeName.site() );
        assertNotNull( contentType );
    }

    @Test
    public void test_stop()
    {
        service.stop();
    }

    protected final ContentType createContentType( final String name, final String displayName )
    {
        return ContentType.create().superType( ContentTypeName.structured() ).displayName( displayName ).name( name ).build();
    }

}