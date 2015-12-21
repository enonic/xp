package com.enonic.xp.launcher;

import java.io.File;

public interface Launcher
{
    void start()
        throws Exception;

    void setListener( LauncherListener listener );

    void stop();

    boolean hasArg( String value );

    File getHomeDir();

    String getHttpUrl();
}
