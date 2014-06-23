package com.enonic.wem.launcher.home;

import java.io.File;

public final class HomeDir
{
    private final File dir;

    public HomeDir( final File dir )
    {
        this.dir = dir.getAbsoluteFile();
    }

    public File toFile()
    {
        return this.dir;
    }

    public String toString()
    {
        return this.dir.toString();
    }
}
