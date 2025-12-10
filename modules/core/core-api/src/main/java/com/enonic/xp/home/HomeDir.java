package com.enonic.xp.home;


import java.io.File;
import java.nio.file.Path;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.server.ServerInfo;

@PublicApi
public final class HomeDir
{
    private static HomeDir homeDir;

    private final Path dir;

    private HomeDir( final Path dir )
    {
        this.dir = dir;
    }

    public File toFile()
    {
        return this.dir.toFile();
    }

    public Path toPath()
    {
        return this.dir;
    }

    @Override
    public String toString()
    {
        return this.dir.toString();
    }

    public static HomeDir get()
    {
        if ( homeDir != null )
        {
            return homeDir;
        }

        return new HomeDir( ServerInfo.get().getHomeDir().toPath().toAbsolutePath().normalize() );
    }

    static void set( final Path dir )
    {
        homeDir = dir == null ? null : new HomeDir( dir );
    }
}
