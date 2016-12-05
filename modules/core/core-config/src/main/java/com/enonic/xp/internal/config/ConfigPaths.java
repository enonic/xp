package com.enonic.xp.internal.config;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

final class ConfigPaths
    implements Iterable<File>
{
    private final static ConfigPaths INSTANCE = new ConfigPaths( System.getProperties() );

    private final ImmutableList<File> list;

    ConfigPaths( final Properties props )
    {
        this( props.getProperty( "xp.config.paths", "" ) );
    }

    ConfigPaths( final String propValue )
    {
        final Iterable<String> it = Splitter.on( "," ).omitEmptyStrings().trimResults().split( propValue );
        final ImmutableList.Builder<File> builder = ImmutableList.builder();
        for ( final String path : it )
        {
            builder.add( new File( path ) );
        }

        this.list = builder.build();
    }

    File resolve( final String path )
    {
        for ( final File base : this.list )
        {
            final File file = new File( base, path );
            if ( file.exists() )
            {
                return file;
            }
        }

        return null;
    }

    static ConfigPaths get()
    {
        return INSTANCE;
    }

    @Override
    public Iterator<File> iterator()
    {
        return this.list.iterator();
    }
}
