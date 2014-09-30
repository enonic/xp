package com.enonic.wem.core.config;

import java.io.File;
import java.nio.file.Path;

public final class SystemConfigImpl
    implements SystemConfig
{
    private final ConfigProperties config;

    public SystemConfigImpl( final ConfigProperties config )
    {
        this.config = config;
    }

    @Override
    public File getHomeDir()
    {
        return new File( this.config.get( "cms.home" ) );
    }

    @Override
    public File getDataDir()
    {
        return new File( getHomeDir(), "data" );
    }

    @Override
    public File getBlobStoreDir()
    {
        return getSharedDir().resolve( "blobs" ).toFile();
    }

    @Override
    public File getConfigDir()
    {
        return new File( getHomeDir(), "config" );
    }


    @Override
    public Path getTemplatesDir()
    {
        return getSharedConfigDir().resolve( "templates" );
    }

    @Override
    public Path getSharedDir()
    {
        return getHomeDir().toPath().resolve( "shared" );
    }

    @Override
    public Path getSharedConfigDir()
    {
        return getSharedDir().resolve( "config" );
    }

    @Override
    public ConfigProperties getRawConfig()
    {
        return this.config;
    }
}
