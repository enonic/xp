package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Optional;

import org.osgi.framework.Bundle;

final class BundleClassLoader
    extends ClassLoader
{
    private final Bundle bundle;

    public BundleClassLoader( final Bundle bundle )
    {
        this.bundle = bundle;
    }

    @Override
    protected Class<?> findClass( final String name )
        throws ClassNotFoundException
    {
        return this.bundle.loadClass( name );
    }

    @Override
    protected URL findResource( final String name )
    {
        return this.bundle.getResource( name );
    }

    @Override
    protected Enumeration<URL> findResources( final String name )
        throws IOException
    {
        return Optional.ofNullable( this.bundle.getResources( name ) ).orElse( Collections.emptyEnumeration() );
    }
}
