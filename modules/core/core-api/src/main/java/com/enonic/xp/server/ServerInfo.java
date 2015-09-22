package com.enonic.xp.server;

import java.io.File;

import com.google.common.annotations.Beta;

@Beta
public interface ServerInfo
{
    String getName();

    File getHomeDir();

    File getInstallDir();

    BuildInfo getBuildInfo();
}
