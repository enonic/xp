package com.enonic.xp.core.impl.app;

import java.net.URL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.service.component.ComponentContext;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.support.ResourceTestHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ApplicationDescriptorServiceImplTest
{

    private static final String EXISTING_APP_BUNDLE_SYMBOLIC_NAME = "existing-site-bundle";

    private static final String EXISTING_NON_APP_BUNDLE_SYMBOLIC_NAME = "existing-non-site-bundle";

    private static final String BUNDLE_SYMBOLIC_NAME = "bundle";

    private static final String APP_DESCRIPTOR_FILENAME = "application.xml";

    private ResourceTestHelper resourceTestHelper;

    private ApplicationDescriptorServiceImpl appDescriptorService;

    private ComponentContext componentContext;

    @BeforeEach
    void setup()
    {
        resourceTestHelper = new ResourceTestHelper( this );

        Bundle existingAppBundle = Mockito.mock( Bundle.class );
        Mockito.when( existingAppBundle.getSymbolicName() ).thenReturn( EXISTING_APP_BUNDLE_SYMBOLIC_NAME );
        final URL resource = resourceTestHelper.getTestResource( APP_DESCRIPTOR_FILENAME );
        Mockito.when( existingAppBundle.getResource( APP_DESCRIPTOR_FILENAME ) ).thenReturn( resource );
        Mockito.when( existingAppBundle.getEntry( APP_DESCRIPTOR_FILENAME ) ).thenReturn( resource );
        Mockito.when( existingAppBundle.getState() ).thenReturn( Bundle.ACTIVE );

        Bundle bundle = Mockito.mock( Bundle.class );

        Bundle existingNonAppBundle = Mockito.mock( Bundle.class );
        Mockito.when( existingNonAppBundle.getSymbolicName() ).thenReturn( EXISTING_NON_APP_BUNDLE_SYMBOLIC_NAME );
        Mockito.when( existingNonAppBundle.getState() ).thenReturn( Bundle.ACTIVE );

        BundleContext bundleContext = Mockito.mock( BundleContext.class );
        Mockito.when( bundleContext.getBundles() ).thenReturn( new Bundle[]{existingAppBundle, existingNonAppBundle} );

        componentContext = Mockito.mock( ComponentContext.class );
        Mockito.when( componentContext.getBundleContext() ).thenReturn( bundleContext );

        appDescriptorService = new ApplicationDescriptorServiceImpl();
    }


    @Test
    void test_start()
    {
        appDescriptorService.start( componentContext );

        assertNotNull( appDescriptorService.get( ApplicationKey.from( EXISTING_APP_BUNDLE_SYMBOLIC_NAME ) ) );
        assertEquals( null, appDescriptorService.get( ApplicationKey.from( EXISTING_NON_APP_BUNDLE_SYMBOLIC_NAME ) ) );
    }

    @Test
    void test_bundle_lifecycle()
    {
        Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( BUNDLE_SYMBOLIC_NAME );
        final URL resource = resourceTestHelper.getTestResource( APP_DESCRIPTOR_FILENAME );
        Mockito.when( bundle.getResource( APP_DESCRIPTOR_FILENAME ) ).thenReturn( resource );
        Mockito.when( bundle.getEntry( APP_DESCRIPTOR_FILENAME ) ).thenReturn( resource );
        Mockito.when( bundle.getState() ).thenReturn( Bundle.ACTIVE );

        appDescriptorService.start( componentContext );
        assertEquals( null, appDescriptorService.get( ApplicationKey.from( BUNDLE_SYMBOLIC_NAME ) ) );

        appDescriptorService.bundleChanged( new BundleEvent( BundleEvent.INSTALLED, bundle ) );
        assertNotNull( appDescriptorService.get( ApplicationKey.from( BUNDLE_SYMBOLIC_NAME ) ) );

        appDescriptorService.bundleChanged( new BundleEvent( BundleEvent.UNINSTALLED, bundle ) );
        assertEquals( null, appDescriptorService.get( ApplicationKey.from( BUNDLE_SYMBOLIC_NAME ) ) );

        appDescriptorService.bundleChanged( new BundleEvent( BundleEvent.UPDATED, bundle ) );
        assertNotNull( appDescriptorService.get( ApplicationKey.from( BUNDLE_SYMBOLIC_NAME ) ) );
    }
}
