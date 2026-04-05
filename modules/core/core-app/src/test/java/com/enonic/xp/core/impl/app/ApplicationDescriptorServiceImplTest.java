package com.enonic.xp.core.impl.app;

import java.net.URL;
import java.util.Hashtable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.service.component.ComponentContext;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.support.ResourceTestHelper;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ApplicationDescriptorServiceImplTest
{
    private static final String APP_DESCRIPTOR_FILENAME = "application.yml";

    private ResourceTestHelper resourceTestHelper;

    private ApplicationDescriptorServiceImpl appDescriptorService;

    private ComponentContext componentContext;

    @BeforeEach
    void setup()
    {
        resourceTestHelper = new ResourceTestHelper( this );

        final Bundle appBundle = mockAppBundle( "com.enonic.myapp" );
        final Bundle nonAppBundle = mockNonAppBundle( "com.enonic.nonapp" );

        BundleContext bundleContext = Mockito.mock( BundleContext.class );
        Mockito.when( bundleContext.getBundles() ).thenReturn( new Bundle[]{appBundle, nonAppBundle} );

        componentContext = Mockito.mock( ComponentContext.class );
        Mockito.when( componentContext.getBundleContext() ).thenReturn( bundleContext );

        appDescriptorService = new ApplicationDescriptorServiceImpl();
    }

    @Test
    void start()
    {
        appDescriptorService.start( componentContext );

        assertNotNull( appDescriptorService.get( ApplicationKey.from( "com.enonic.myapp" ) ) );
        assertNull( appDescriptorService.get( ApplicationKey.from( "com.enonic.nonapp" ) ) );
    }

    @Test
    void bundle_lifecycle()
    {
        final Bundle bundle = mockAppBundle( "com.enonic.newapp" );

        appDescriptorService.start( componentContext );
        assertNull( appDescriptorService.get( ApplicationKey.from( "com.enonic.newapp" ) ) );

        appDescriptorService.bundleChanged( new BundleEvent( BundleEvent.INSTALLED, bundle ) );
        assertNotNull( appDescriptorService.get( ApplicationKey.from( "com.enonic.newapp" ) ) );

        appDescriptorService.bundleChanged( new BundleEvent( BundleEvent.UNINSTALLED, bundle ) );
        assertNull( appDescriptorService.get( ApplicationKey.from( "com.enonic.newapp" ) ) );

        appDescriptorService.bundleChanged( new BundleEvent( BundleEvent.UPDATED, bundle ) );
        assertNotNull( appDescriptorService.get( ApplicationKey.from( "com.enonic.newapp" ) ) );
    }

    @Test
    void app_without_descriptor_file()
    {
        final Hashtable<String, String> headers = new Hashtable<>();
        headers.put( "X-Bundle-Type", "application" );

        final Bundle appBundleNoDescriptor = Mockito.mock( Bundle.class );
        Mockito.when( appBundleNoDescriptor.getSymbolicName() ).thenReturn( "com.enonic.nodescriptor" );
        Mockito.when( appBundleNoDescriptor.getState() ).thenReturn( Bundle.ACTIVE );
        Mockito.when( appBundleNoDescriptor.getHeaders() ).thenReturn( headers );

        appDescriptorService.start( componentContext );

        appDescriptorService.bundleChanged( new BundleEvent( BundleEvent.INSTALLED, appBundleNoDescriptor ) );

        assertNotNull( appDescriptorService.get( ApplicationKey.from( "com.enonic.nodescriptor" ) ) );
    }

    private Bundle mockAppBundle( final String symbolicName )
    {
        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( symbolicName );
        final URL resource = resourceTestHelper.getTestResource( APP_DESCRIPTOR_FILENAME );
        Mockito.when( bundle.getResource( APP_DESCRIPTOR_FILENAME ) ).thenReturn( resource );
        Mockito.when( bundle.getEntry( APP_DESCRIPTOR_FILENAME ) ).thenReturn( resource );
        Mockito.when( bundle.getState() ).thenReturn( Bundle.ACTIVE );
        Mockito.when( bundle.getHeaders() ).thenReturn( new Hashtable<>() );
        return bundle;
    }

    private static Bundle mockNonAppBundle( final String symbolicName )
    {
        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( symbolicName );
        Mockito.when( bundle.getState() ).thenReturn( Bundle.ACTIVE );
        Mockito.when( bundle.getHeaders() ).thenReturn( new Hashtable<>() );
        return bundle;
    }
}
