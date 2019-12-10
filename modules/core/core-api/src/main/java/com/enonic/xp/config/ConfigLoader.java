package com.enonic.xp.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

final class ConfigLoader
{
    private final Class context;

    public ConfigLoader( final Class context )
    {
        this.context = context;
    }

    public Properties load( final String name )
    {
        try (final InputStream in = findFile( name ))
        {
            final Properties props = new Properties();
            props.load( in );
            return props;
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private InputStream findFile( final String name )
    {
        final InputStream in = this.context.getResourceAsStream( name );
        if ( in == null )
        {
            throw new IllegalArgumentException( "Failed to find resource [" + name + "]" );
        }

        return in;
    }
}
