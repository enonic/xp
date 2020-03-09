package com.enonic.xp.launcher;

import java.io.File;

public interface Launcher
{
    void start()
        throws Exception;

    @Deprecated(forRemoval = true)
    void setListener( LauncherListener listener );

    void stop();

    @Deprecated(forRemoval = true)
    boolean hasArg( String value );

    @Deprecated(forRemoval = true)
    File getHomeDir();

    @Deprecated(forRemoval = true)
    String getHttpUrl();
}
