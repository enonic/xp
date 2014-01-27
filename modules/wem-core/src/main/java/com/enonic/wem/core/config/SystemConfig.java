package com.enonic.wem.core.config;

import java.io.File;
import java.nio.file.Path;

public interface SystemConfig
{
    public File getHomeDir();

    public File getDataDir();

    public File getBlobStoreDir();

    public File getConfigDir();

    public Path getModulesDir();

    public Path getTemplatesDir();

    public Path getSchemasDir();

    public Path getContentTypesDir();

    public Path getMixinsDir();

    public boolean isMigrateEnabled();

    public String getMigrateJdbcDriver();

    public String getMigrateJdbcUrl();

    public String getMigrateJdbcUser();

    public String getMigrateJdbcPassword();

    public Path getSharedDir();

    public Path getSharedConfigDir();

    public ConfigProperties getRawConfig();
}
