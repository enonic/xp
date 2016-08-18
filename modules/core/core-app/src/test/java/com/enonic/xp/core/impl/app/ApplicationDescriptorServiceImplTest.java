package com.enonic.xp.core.impl.app;

import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.service.component.ComponentContext;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.support.ResourceTestHelper;

public class ApplicationDescriptorServiceImplTest
{

    private static final String EXISTING_APP_BUNDLE_SYMBOLIC_NAME = "existing-site-bundle";

    private static final String EXISTING_NON_APP_BUNDLE_SYMBOLIC_NAME = "existing-non-site-bundle";

    private static final String BUNDLE_SYMBOLIC_NAME = "bundle";

    private static final String APP_DESCRIPTOR_FILENAME = "application.xml";

    private ResourceTestHelper resourceTestHelper;

    private ApplicationDescriptorServiceImpl appDescriptorService;

    private ComponentContext componentContext;

    @Before
    public void setup()
        throws Exception
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
    public void test_start()
    {
        appDescriptorService.start( componentContext );

        Assert.assertNotNull( appDescriptorService.get( ApplicationKey.from( EXISTING_APP_BUNDLE_SYMBOLIC_NAME ) ) );
        Assert.assertEquals( null, appDescriptorService.get( ApplicationKey.from( EXISTING_NON_APP_BUNDLE_SYMBOLIC_NAME ) ) );
    }

    @Test
    public void test_bundle_lifecycle()
        throws Exception
    {
        Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( BUNDLE_SYMBOLIC_NAME );
        final URL resource = resourceTestHelper.getTestResource( APP_DESCRIPTOR_FILENAME );
        Mockito.when( bundle.getResource( APP_DESCRIPTOR_FILENAME ) ).thenReturn( resource );
        Mockito.when( bundle.getEntry( APP_DESCRIPTOR_FILENAME ) ).thenReturn( resource );
        Mockito.when( bundle.getState() ).thenReturn( Bundle.ACTIVE );

        appDescriptorService.start( componentContext );
        Assert.assertEquals( null, appDescriptorService.get( ApplicationKey.from( BUNDLE_SYMBOLIC_NAME ) ) );

        appDescriptorService.bundleChanged( new BundleEvent( BundleEvent.INSTALLED, bundle ) );
        Assert.assertNotNull( appDescriptorService.get( ApplicationKey.from( BUNDLE_SYMBOLIC_NAME ) ) );

        appDescriptorService.bundleChanged( new BundleEvent( BundleEvent.UNINSTALLED, bundle ) );
        Assert.assertEquals( null, appDescriptorService.get( ApplicationKey.from( BUNDLE_SYMBOLIC_NAME ) ) );

        appDescriptorService.bundleChanged( new BundleEvent( BundleEvent.UPDATED, bundle ) );
        Assert.assertNotNull( appDescriptorService.get( ApplicationKey.from( BUNDLE_SYMBOLIC_NAME ) ) );
    }
}