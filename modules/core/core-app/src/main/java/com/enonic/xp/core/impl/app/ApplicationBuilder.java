package com.enonic.xp.core.impl.app;

import org.osgi.framework.Bundle;

import com.google.common.base.Preconditions;

import com.enonic.xp.app.Application;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.Configuration;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;

public final class ApplicationBuilder
{
    private Bundle bundle;

    private ApplicationUrlResolver urlResolver;

    private ClassLoader classLoader;

    private Configuration config;

    public ApplicationBuilder bundle( final Bundle bundle )
    {
        this.bundle = bundle;
        return this;
    }

    public ApplicationBuilder urlResolver( final ApplicationUrlResolver urlResolver )
    {
        this.urlResolver = urlResolver;
        return this;
    }

    public ApplicationBuilder classLoader( final ClassLoader classLoader )
    {
        this.classLoader = classLoader;
        return this;
    }

    public ApplicationBuilder config( final Configuration config )
    {
        this.config = config;
        return this;
    }

    public Application build()
    {
        Preconditions.checkNotNull( this.bundle, "bundle is required" );
        Preconditions.checkNotNull( this.urlResolver, "urlResolver is required" );

        if ( this.classLoader == null )
        {
            this.classLoader = new BundleClassLoader( this.bundle );
        }

        if ( this.config == null )
        {
            this.config = ConfigBuilder.create().build();
        }

        return new ApplicationImpl( this.bundle, this.urlResolver, this.classLoader, this.config );
    }
}
