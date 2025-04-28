package com.enonic.xp.lib.admin;

import com.enonic.xp.server.ServerInfo;
import com.enonic.xp.server.VersionInfo;

public final class AdminLibHelper
{
    public String getInstallation()
    {
        return ServerInfo.get().getName();
    }

    public String getVersion()
    {
        return VersionInfo.get().getVersion();
    }
}
