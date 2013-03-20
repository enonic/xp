package com.enonic.wem.core.config;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import com.enonic.wem.core.home.HomeDir;

public final class ConfigModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        // Do nothing
    }

    @Provides
    @Singleton
    public ConfigProperties configProperties( final HomeDir homeDir )
    {
        final ConfigLoader loader = new ConfigLoader( homeDir );
        return loader.load();
    }

    @Provides
    @Singleton
    public SystemConfig systemConfig( final ConfigProperties props )
    {
        return new SystemConfigImpl( props );
    }
}
