package com.enonic.xp.core.impl.schema.mixin;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.service.component.ComponentContext;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.core.impl.schema.AbstractBundleTest;
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

    private ApplicationKey myApplicationKey;

    private Mixin mixin1;

    private Application myApplication;

    private ApplicationService applicationService;

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
        myApplicationKey = ApplicationKey.from( myBundle );
        this.mixin1 = createMixin( "module2:mixin1" );
        myApplication = Mockito.mock( Application.class );
        Mockito.when( myApplication.getKey() ).thenReturn( myApplicationKey );
        Mockito.when( myApplication.getBundle() ).thenReturn( myBundle );

        //Mocks the module service
        applicationService = Mockito.mock( ApplicationService.class );

        //Mocks the ComponentContext
        final ComponentContext componentContext = Mockito.mock( ComponentContext.class );
        Mockito.when( componentContext.getBundleContext() ).thenReturn( this.serviceRegistry.getBundleContext() );

        //Creates the service to test
        service = new MixinServiceImpl();
        service.setApplicationService( applicationService );

        //Starts the service
        service.start( componentContext );
    }

    @Test
    public void test_empty()
    {
        Mockito.when( applicationService.getAllApplications() ).thenReturn( Applications.empty() );

        Mixins mixins = service.getAll();
        assertNotNull( mixins );
        assertEquals( 3, mixins.getSize() );

        mixins = service.getByModule( myApplicationKey );
        assertNotNull( mixins );
        assertEquals( 0, mixins.getSize() );

        Mixin mixin = service.getByName( this.mixin1.getName() );
        assertEquals( null, mixin );
    }

    @Test
    public void test_get_by_local_name()
    {

        Applications applications = Applications.from( myApplication );
        Mockito.when( applicationService.getAllApplications() ).thenReturn( applications );
        Mockito.when( applicationService.getApplication( myApplicationKey ) ).thenReturn( myApplication );

        Mixin mixin = service.getByLocalName( "mixin1" );
        assertNotNull( mixin );
        assertEquals( mixin.getName(), this.mixin1.getName() );
    }

    @Test
    public void test_get_by_content_type()
    {

        Applications applications = Applications.from( myApplication );
        Mockito.when( applicationService.getAllApplications() ).thenReturn( applications );
        Mockito.when( applicationService.getApplication( myApplicationKey ) ).thenReturn( myApplication );

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

        Applications applications = Applications.from( myApplication );
        Mockito.when( applicationService.getAllApplications() ).thenReturn( applications );
        Mockito.when( applicationService.getApplication( myApplicationKey ) ).thenReturn( myApplication );

        Mixins mixins = service.getAll();
        assertNotNull( mixins );
        assertEquals( 4, mixins.getSize() );

        mixins = service.getByModule( myApplicationKey );
        assertNotNull( mixins );
        assertEquals( 1, mixins.getSize() );

        Mixin mixin = service.getByName( this.mixin1.getName() );
        assertNotNull( mixin );

        Mockito.when( applicationService.getAllApplications() ).thenReturn( Applications.empty() );
        Mockito.when( applicationService.getApplication( myApplicationKey ) ).thenReturn( null );
        service.bundleChanged( new BundleEvent( BundleEvent.UNINSTALLED, myBundle ) );

        test_empty();
    }

    @Test
    public void test_get_system_module()
    {

        Mockito.when( applicationService.getAllApplications() ).thenReturn( Applications.empty() );

        Mixins mixins = service.getAll();
        assertNotNull( mixins );
        assertEquals( 3, mixins.getSize() );

        mixins = service.getByModule( ApplicationKey.MEDIA_MOD );
        assertNotNull( mixins );
        assertEquals( 2, mixins.getSize() );

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
