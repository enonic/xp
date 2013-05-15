package com.enonic.wem.core.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import com.enonic.wem.core.home.HomeDir;

public final class ConfigModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
    }

    @Provides
    @Singleton
    public ConfigProperties config( final HomeDir homeDir )
    {
        final ConfigLoader loader = new ConfigLoader( homeDir );
        return loader.load();
    }

    @Provides
    @Singleton
    public SystemConfig systemConfig( final ConfigProperties properties )
    {
        return new SystemConfigImpl( properties );
    }
}
