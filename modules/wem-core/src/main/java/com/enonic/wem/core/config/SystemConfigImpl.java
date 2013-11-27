package com.enonic.wem.core.config;

import java.io.File;

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
        return new File( getHomeDir(), "blob-store" );
    }

    @Override
    public File getConfigDir()
    {
        return new File( getHomeDir(), "config" );
    }

    @Override
    public File getModulesDir()
    {
        return new File( getConfigDir(), "modules" );
    }

    @Override
    public File getTemplatesDir()
    {
        return new File( getConfigDir(), "templates" );
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
    public ConfigProperties getRawConfig()
    {
        return this.config;
    }
}
