package com.enonic.wem.admin.config;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.enonic.wem.guice.Configuration;

@Singleton
public final class AdminConfigImpl
    implements AdminConfig
{
    private final Configuration config;

    @Inject
    public AdminConfigImpl( final Configuration config )
    {
        this.config = config;
    }

    @Override
    public File getResourcesDevDir()
    {
        final String value = this.config.getOrDefault( "resourcesDevDir", null );
        return value != null ? new File( value ) : null;
    }
}
