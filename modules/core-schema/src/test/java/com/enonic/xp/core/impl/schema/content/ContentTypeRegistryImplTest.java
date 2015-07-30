package com.enonic.xp.core.impl.schema.content;

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
import com.enonic.xp.schema.content.ContentTypes;

import static org.junit.Assert.*;

public class ContentTypeRegistryImplTest
    extends AbstractBundleTest
{

    private Bundle myBundle;

    private ApplicationKey myApplicationKey;

    private ContentType myContentType;

    private Application myApplication;

    private ApplicationService applicationService;

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
        myApplication = Mockito.mock( Application.class );
        Mockito.when( myApplication.getKey() ).thenReturn( myApplicationKey );
        Mockito.when( myApplication.getBundle() ).thenReturn( myBundle );

        //Mocks the module service
        applicationService = Mockito.mock( ApplicationService.class );

        //Mocks the ComponentContext
        final ComponentContext componentContext = Mockito.mock( ComponentContext.class );
        Mockito.when( componentContext.getBundleContext() ).thenReturn( this.serviceRegistry.getBundleContext() );

        //Creates the service to test
        service = new ContentTypeRegistryImpl();
        service.setApplicationService( applicationService );

        //Starts the service
        service.start( componentContext );
    }

    @Test
    public void test_empty()
    {
        Mockito.when( applicationService.getAllApplications() ).thenReturn( Applications.empty() );

        ContentTypes contentTypes = service.getAll();
        assertNotNull( contentTypes );
        assertTrue( contentTypes.getSize() > 20 );

        contentTypes = service.getByApplication( myApplicationKey );
        assertNotNull( contentTypes );
        assertEquals( 0, contentTypes.getSize() );

        ContentType contentType = service.get( this.myContentType.getName() );
        assertEquals( null, contentType );
    }

    @Test
    public void test_add_removal_module()
    {

        Applications applications = Applications.from( myApplication );
        Mockito.when( applicationService.getAllApplications() ).thenReturn( applications );
        Mockito.when( applicationService.getApplication( myApplicationKey ) ).thenReturn( myApplication );

        ContentTypes contentTypes = service.getAll();
        assertNotNull( contentTypes );
        assertTrue( contentTypes.getSize() > 20 );

        contentTypes = service.getByApplication( myApplicationKey );
        assertNotNull( contentTypes );
        assertEquals( 1, contentTypes.getSize() );

        ContentType contentType = service.get( this.myContentType.getName() );
        assertNotNull( contentType );

        Mockito.when( applicationService.getAllApplications() ).thenReturn( Applications.empty() );
        Mockito.when( applicationService.getApplication( myApplicationKey ) ).thenReturn( null );
        service.bundleChanged( new BundleEvent( BundleEvent.UNINSTALLED, myBundle ) );

        test_empty();
    }

    @Test
    public void test_get_system_module()
    {

        Mockito.when( applicationService.getAllApplications() ).thenReturn( Applications.empty() );

        ContentTypes contentTypes = service.getAll();
        assertNotNull( contentTypes );
        assertTrue( contentTypes.getSize() > 20 );

        ContentType contentType = service.get( ContentTypeName.folder() );
        assertNotNull( contentType );

        contentTypes = service.getByApplication( ApplicationKey.BASE );
        assertNotNull( contentTypes );
        assertEquals( contentTypes.getSize(), 5 );

        contentTypes = service.getByApplication( ApplicationKey.PORTAL );
        assertNotNull( contentTypes );
        assertEquals( contentTypes.getSize(), 3 );

        contentTypes = service.getByApplication( ApplicationKey.MEDIA_MOD );
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