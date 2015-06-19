package com.enonic.xp.core.impl.schema.mixin;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.service.component.ComponentContext;

import com.enonic.xp.core.impl.schema.AbstractBundleTest;
import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleService;
import com.enonic.xp.module.Modules;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinNames;
import com.enonic.xp.schema.mixin.Mixins;

import static org.junit.Assert.*;

public class MixinServiceImplTest
    extends AbstractBundleTest
{

    private Bundle myBundle;

    private ModuleKey myModuleKey;

    private Mixin mixin1;

    private Module myModule;

    private ModuleService moduleService;

    private MixinServiceImpl service;

    @Before
    @Override
    public void setup()
        throws Exception
    {
        super.setup();

        //Mocks a module
        startBundles( newBundle( "module2" ) );
        myBundle = findBundle( "module2" );
        myModuleKey = ModuleKey.from( myBundle );
        this.mixin1 = createMixin( "module2:mixin1" );
        myModule = Mockito.mock( Module.class );
        Mockito.when( myModule.getKey() ).thenReturn( myModuleKey );
        Mockito.when( myModule.getBundle() ).thenReturn( myBundle );

        //Mocks the module service
        moduleService = Mockito.mock( ModuleService.class );

        //Mocks the ComponentContext
        final ComponentContext componentContext = Mockito.mock( ComponentContext.class );
        Mockito.when( componentContext.getBundleContext() ).thenReturn( this.serviceRegistry.getBundleContext() );

        //Creates the service to test
        service = new MixinServiceImpl();
        service.setModuleService( moduleService );

        //Starts the service
        service.start( componentContext );
    }

    @Test
    public void test_empty()
    {
        Mockito.when( moduleService.getAllModules() ).thenReturn( Modules.empty() );

        Mixins mixins = service.getAll();
        assertNotNull( mixins );
        assertEquals( 3, mixins.getSize() );

        mixins = service.getByModule( myModuleKey );
        assertNotNull( mixins );
        assertEquals( 0, mixins.getSize() );

        Mixin mixin = service.getByName( this.mixin1.getName() );
        assertEquals( null, mixin );
    }

    @Test
    public void test_get_by_local_name()
    {

        Modules modules = Modules.from( myModule );
        Mockito.when( moduleService.getAllModules() ).thenReturn( modules );
        Mockito.when( moduleService.getModule( myModuleKey ) ).thenReturn( myModule );

        Mixin mixin = service.getByLocalName( "mixin1" );
        assertNotNull( mixin );
        assertEquals( mixin.getName(), this.mixin1.getName() );
    }

    @Test
    public void test_get_by_content_type()
    {

        Modules modules = Modules.from( myModule );
        Mockito.when( moduleService.getAllModules() ).thenReturn( modules );
        Mockito.when( moduleService.getModule( myModuleKey ) ).thenReturn( myModule );

        ContentType contentType = ContentType.create().
            superType( ContentTypeName.structured() ).
            name( "module2:mixin1" ).
            metadata( MixinNames.from( "module2:mixin1", "module2:mixin2" ) ).
            build();

        Mixins mixins = service.getByContentType( contentType );
        assertNotNull( mixins );
        assertEquals( 1, mixins.getSize() );

    }

    @Test
    public void test_add_removal_module()
    {

        Modules modules = Modules.from( myModule );
        Mockito.when( moduleService.getAllModules() ).thenReturn( modules );
        Mockito.when( moduleService.getModule( myModuleKey ) ).thenReturn( myModule );

        Mixins mixins = service.getAll();
        assertNotNull( mixins );
        assertEquals( 4, mixins.getSize() );

        mixins = service.getByModule( myModuleKey );
        assertNotNull( mixins );
        assertEquals( 1, mixins.getSize() );

        Mixin mixin = service.getByName( this.mixin1.getName() );
        assertNotNull( mixin );

        Mockito.when( moduleService.getAllModules() ).thenReturn( Modules.empty() );
        Mockito.when( moduleService.getModule( myModuleKey ) ).thenReturn( null );
        service.bundleChanged( new BundleEvent( BundleEvent.UNINSTALLED, myBundle ) );

        test_empty();
    }

    @Test
    public void test_get_system_module()
    {

        Mockito.when( moduleService.getAllModules() ).thenReturn( Modules.empty() );

        Mixins mixins = service.getAll();
        assertNotNull( mixins );
        assertEquals( 3, mixins.getSize() );

        mixins = service.getByModule( ModuleKey.SYSTEM );
        assertNotNull( mixins );
        assertEquals( 3, mixins.getSize() );

        Mixin mixin = service.getByName( BuiltinMixinsLoader.GPS_INFO_METADATA_NAME );
        assertNotNull( mixin );

        mixin = service.getByName( BuiltinMixinsLoader.IMAGE_INFO_METADATA_NAME );
        assertNotNull( mixin );

        mixin = service.getByName( BuiltinMixinsLoader.PHOTO_INFO_METADATA_NAME );
        assertNotNull( mixin );
    }

    @Test
    public void test_stop()
    {
        service.stop();
    }

    private Mixin createMixin( final String name )
    {
        return Mixin.create().name( name ).build();
    }

}
