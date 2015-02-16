package com.enonic.xp.core.impl.module;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.kalpatec.pojosr.framework.PojoServiceRegistryFactoryImpl;
import de.kalpatec.pojosr.framework.launch.BundleDescriptor;
import de.kalpatec.pojosr.framework.launch.PojoServiceRegistry;

import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleVersion;

import static org.junit.Assert.*;

public class ModuleBuilder2Test
{
    private PojoServiceRegistry serviceRegistry;

    @Before
    public void setup()
        throws Exception
    {
        final Map<String, Object> config = Maps.newHashMap();
        this.serviceRegistry = new PojoServiceRegistryFactoryImpl().newPojoServiceRegistry( config );
    }

    @Test
    public void buildModule()
        throws Exception
    {
        final Map<String, String> headers = Maps.newHashMap();
        headers.put( Constants.BUNDLE_SYMBOLICNAME, "com.enonic.test.mybundle" );
        headers.put( Constants.BUNDLE_VERSION, "1.0.0" );
        headers.put( Constants.BUNDLE_NAME, "My test module" );
        headers.put( ModuleBuilder2.X_MODULE_URL, "http://enonic.com/path/to/module" );
        headers.put( ModuleBuilder2.X_VENDOR_NAME, "Enonic AS" );
        headers.put( ModuleBuilder2.X_VENDOR_URL, "http://enonic.com" );
        headers.put( ModuleBuilder2.X_SYSTEM_VERSION, "[5.0,6.0)" );

        final ModuleBuilder2 builder = new ModuleBuilder2();
        builder.bundle( newBundle( "bundle1", headers ) );

        final Module module = builder.build();
        assertNotNull( module );
        assertEquals( ModuleKey.from( "com.enonic.test.mybundle" ), module.getKey() );
        assertEquals( ModuleVersion.from( "1.0.0" ), module.getVersion() );
        assertEquals( "My test module", module.getDisplayName() );
        assertEquals( "http://enonic.com/path/to/module", module.getUrl() );
        assertEquals( "Enonic AS", module.getVendorName() );
        assertEquals( "http://enonic.com", module.getVendorUrl() );
        assertEquals( "[5.0,6.0)", module.getSystemVersion() );
    }

    private Bundle newBundle( final String name, final Map<String, String> headers )
        throws Exception
    {
        final BundleDescriptor descriptor = newBundleDescriptor( name, headers );
        this.serviceRegistry.startBundles( Lists.newArrayList( descriptor ) );
        return this.serviceRegistry.getBundleContext().getBundles()[1];
    }

    private BundleDescriptor newBundleDescriptor( final String name, final Map<String, String> headers )
    {
        final URL url = getClass().getResource( "/bundles/" + name + "/" );
        final URLClassLoader loader = new URLClassLoader( new URL[]{url}, null );
        return new BundleDescriptor( loader, url, headers );
    }
}
