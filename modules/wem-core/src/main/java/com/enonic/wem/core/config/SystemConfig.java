package com.enonic.wem.core.config;

import java.io.File;

public interface SystemConfig
{
    public File getHomeDir();

    public File getDataDir();

    public File getConfigDir();

    public File getModuleDir();

    public boolean isMigrateEnabled();

    public String getMigrateJdbcDriver();

    public String getMigrateJdbcUrl();

    public String getMigrateJdbcUser();

    public String getMigrateJdbcPassword();

    public ConfigProperties getRawConfig();
}
