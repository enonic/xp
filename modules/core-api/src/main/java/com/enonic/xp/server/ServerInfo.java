package com.enonic.xp.server;

import java.io.File;

public interface ServerInfo
{
    public String getName();

    public File getHomeDir();

    public File getInstallDir();

    public BuildInfo getBuildInfo();
}
