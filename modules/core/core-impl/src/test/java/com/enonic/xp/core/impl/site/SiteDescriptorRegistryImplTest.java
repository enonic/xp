package com.enonic.xp.core.impl.site;

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

public class SiteDescriptorRegistryImplTest
{

    private static final String EXISTING_SITE_BUNDLE_SYMBOLIC_NAME = "existing-site-bundle";

    private static final String EXISTING_NON_SITE_BUNDLE_SYMBOLIC_NAME = "existing-non-site-bundle";

    private static final String BUNDLE_SYMBOLIC_NAME = "bundle";

    private static final String TEST_SITE_DESCRIPTOR_FILENAME = "site.xml";

    private static final String SITE_DESCRIPTOR_FILENAME = "site/site.xml";

    private ResourceTestHelper resourceTestHelper;

    private SiteDescriptorRegistryImpl siteDescriptorRegistry;

    private ComponentContext componentContext;

    @Before
    public void setup()
        throws Exception
    {
        resourceTestHelper = new ResourceTestHelper( this );

        Bundle existingSiteBundle = Mockito.mock( Bundle.class );
        Mockito.when( existingSiteBundle.getSymbolicName() ).thenReturn( EXISTING_SITE_BUNDLE_SYMBOLIC_NAME );
        final URL resource = resourceTestHelper.getTestResource( TEST_SITE_DESCRIPTOR_FILENAME );
        Mockito.when( existingSiteBundle.getResource( SITE_DESCRIPTOR_FILENAME ) ).thenReturn( resource );
        Mockito.when( existingSiteBundle.getEntry( SITE_DESCRIPTOR_FILENAME ) ).thenReturn( resource );
        Mockito.when( existingSiteBundle.getState() ).thenReturn( Bundle.ACTIVE );

        Bundle bundle = Mockito.mock( Bundle.class );

        Bundle existingNonSiteBundle = Mockito.mock( Bundle.class );
        Mockito.when( existingNonSiteBundle.getSymbolicName() ).thenReturn( EXISTING_NON_SITE_BUNDLE_SYMBOLIC_NAME );
        Mockito.when( existingNonSiteBundle.getState() ).thenReturn( Bundle.ACTIVE );

        BundleContext bundleContext = Mockito.mock( BundleContext.class );
        Mockito.when( bundleContext.getBundles() ).thenReturn( new Bundle[]{existingSiteBundle, existingNonSiteBundle} );

        componentContext = Mockito.mock( ComponentContext.class );
        Mockito.when( componentContext.getBundleContext() ).thenReturn( bundleContext );

        siteDescriptorRegistry = new SiteDescriptorRegistryImpl();
    }


    @Test
    public void test_start()
    {
        siteDescriptorRegistry.start( componentContext );

        Assert.assertNotNull( siteDescriptorRegistry.get( ApplicationKey.from( EXISTING_SITE_BUNDLE_SYMBOLIC_NAME ) ) );
        Assert.assertEquals( null, siteDescriptorRegistry.get( ApplicationKey.from( EXISTING_NON_SITE_BUNDLE_SYMBOLIC_NAME ) ) );
    }

    @Test
    public void test_bundle_lifecycle()
        throws Exception
    {
        Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( BUNDLE_SYMBOLIC_NAME );
        final URL resource = resourceTestHelper.getTestResource( TEST_SITE_DESCRIPTOR_FILENAME );
        Mockito.when( bundle.getResource( SITE_DESCRIPTOR_FILENAME ) ).thenReturn( resource );
        Mockito.when( bundle.getEntry( SITE_DESCRIPTOR_FILENAME ) ).thenReturn( resource );
        Mockito.when( bundle.getState() ).thenReturn( Bundle.ACTIVE );

        siteDescriptorRegistry.start( componentContext );
        Assert.assertEquals( null, siteDescriptorRegistry.get( ApplicationKey.from( BUNDLE_SYMBOLIC_NAME ) ) );

        siteDescriptorRegistry.bundleChanged( new BundleEvent( BundleEvent.INSTALLED, bundle ) );
        Assert.assertNotNull( siteDescriptorRegistry.get( ApplicationKey.from( BUNDLE_SYMBOLIC_NAME ) ) );

        siteDescriptorRegistry.bundleChanged( new BundleEvent( BundleEvent.UNINSTALLED, bundle ) );
        Assert.assertEquals( null, siteDescriptorRegistry.get( ApplicationKey.from( BUNDLE_SYMBOLIC_NAME ) ) );

        siteDescriptorRegistry.bundleChanged( new BundleEvent( BundleEvent.UPDATED, bundle ) );
        Assert.assertNotNull( siteDescriptorRegistry.get( ApplicationKey.from( BUNDLE_SYMBOLIC_NAME ) ) );
    }
}
