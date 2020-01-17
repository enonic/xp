package com.enonic.xp.elasticsearch.server.impl;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.google.common.base.Strings.isNullOrEmpty;

public final class EsHomeDir
{

    private final Path dir;

    private EsHomeDir( final Path dir )
    {
        this.dir = dir;
    }

    public Path getDir()
    {
        return dir;
    }

    @Override
    public String toString()
    {
        return this.dir.toAbsolutePath().toString();
    }

    public static EsHomeDir get()
    {
        String property = System.getProperty( "es.home" );
        if ( isNullOrEmpty( property ) )
        {
            throw new IllegalArgumentException( "Home dir [es.home] is not set." );
        }

        return new EsHomeDir( Paths.get( property ) );
    }

}
