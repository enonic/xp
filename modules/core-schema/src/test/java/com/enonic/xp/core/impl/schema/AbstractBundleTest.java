package com.enonic.xp.core.impl.schema;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

import org.junit.Before;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.kalpatec.pojosr.framework.PojoServiceRegistryFactoryImpl;
import de.kalpatec.pojosr.framework.launch.BundleDescriptor;
import de.kalpatec.pojosr.framework.launch.PojoServiceRegistry;

public abstract class AbstractBundleTest
{
    protected PojoServiceRegistry serviceRegistry;

    @Before
    public void setup()
        throws Exception
    {
        final Map<String, Object> config = Maps.newHashMap();
        this.serviceRegistry = new PojoServiceRegistryFactoryImpl().newPojoServiceRegistry( config );
    }

    protected final void startBundles( final BundleDescriptor... bundles )
        throws Exception
    {
        this.serviceRegistry.startBundles( Lists.newArrayList( bundles ) );
    }

    protected final BundleDescriptor newBundle( final String name )
    {
        final URL url = getClass().getResource( "/bundles/" + name + "/" );
        final URLClassLoader loader = new URLClassLoader( new URL[]{url}, null );

        final Map<String, String> headers = Maps.newHashMap();
        headers.put( Constants.BUNDLE_SYMBOLICNAME, name );
        headers.put( Constants.BUNDLE_VERSION, "1.0.0" );

        return new BundleDescriptor( loader, url, headers );
    }

    protected final Bundle findBundle( final String name )
    {
        for ( final Bundle bundle : this.serviceRegistry.getBundleContext().getBundles() )
        {
            if ( bundle.getSymbolicName().equals( name ) )
            {
                return bundle;
            }
        }

        return null;
    }
}
