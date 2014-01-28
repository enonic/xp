package com.enonic.wem.core.config;

import java.io.File;
import java.nio.file.Path;

final class SystemConfigImpl
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
        return new File( this.config.getProperty( "cms.home" ) );
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
    public Path getModulesDir()
    {
        return getSharedConfigDir().resolve( "modules" );
    }

    @Override
    public Path getTemplatesDir()
    {
        return getSharedConfigDir().resolve( "templates" );
    }

    @Override
    public Path getSchemasDir()
    {
        return getSharedConfigDir().resolve( "schemas" );
    }

    @Override
    public Path getContentTypesDir()
    {
        return getSchemasDir().resolve( "content-types" );
    }

    @Override
    public Path getMixinsDir()
    {
        return getSchemasDir().resolve( "mixins" );
    }

    @Override
    public Path getRelationshiptTypesDir()
    {
        return getSchemasDir().resolve( "relation-types" );
    }

    @Override
    public boolean isMigrateEnabled()
    {
        return "true".equals( this.config.getProperty( "cms.migrate.enabled" ) );
    }

    @Override
    public String getMigrateJdbcDriver()
    {
        return this.config.getProperty( "cms.migrate.jdbc.driver" );
    }

    @Override
    public String getMigrateJdbcUrl()
    {
        return this.config.getProperty( "cms.migrate.jdbc.url" );
    }

    @Override
    public String getMigrateJdbcUser()
    {
        return this.config.getProperty( "cms.migrate.jdbc.user" );
    }

    @Override
    public String getMigrateJdbcPassword()
    {
        return this.config.getProperty( "cms.migrate.jdbc.password" );
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
