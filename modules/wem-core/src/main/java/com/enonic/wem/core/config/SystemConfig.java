package com.enonic.wem.core.config;

import java.io.File;
import java.nio.file.Path;

public interface SystemConfig
{
    public File getHomeDir();

    public File getDataDir();

    public File getBlobStoreDir();

    public File getConfigDir();

    public Path getTemplatesDir();

    public Path getSharedDir();

    public Path getSharedConfigDir();

    public ConfigProperties getRawConfig();
}
