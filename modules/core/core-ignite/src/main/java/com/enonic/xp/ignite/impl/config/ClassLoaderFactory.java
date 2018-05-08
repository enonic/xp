package com.enonic.xp.ignite.impl.config;

import org.apache.ignite.Ignite;
import org.apache.ignite.osgi.classloaders.ContainerSweepClassLoader;
import org.osgi.framework.BundleContext;

class ClassLoaderFactory
{
    static ClassLoader create( final BundleContext bundleContext )
    {
        return new ContainerSweepClassLoader( bundleContext.getBundle(), Ignite.class.getClassLoader() );
    }
}
