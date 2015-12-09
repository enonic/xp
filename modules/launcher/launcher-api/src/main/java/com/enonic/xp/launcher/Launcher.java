package com.enonic.xp.launcher;

public interface Launcher
{
    void start()
        throws Exception;

    void setListener( LauncherListener listener );

    void stop();

    boolean hasArg( String value );

    String getHttpUrl();
}
