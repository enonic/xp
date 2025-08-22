package com.enonic.xp.core.impl.app;

import java.util.Objects;

import org.osgi.framework.Bundle;

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

    public ApplicationImpl build()
    {
        Objects.requireNonNull( this.bundle, "bundle is required" );
        Objects.requireNonNull( this.urlResolver, "urlResolver is required" );

        if ( this.classLoader == null )
        {
            this.classLoader = new BundleClassLoader( this.bundle );
        }

        return new ApplicationImpl( this.bundle, this.urlResolver, this.classLoader, this.config );
    }
}
