package com.enonic.xp.config;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import com.enonic.xp.util.Exceptions;

final class ConfigLoader
{
    private final Class context;

    public ConfigLoader( final Class context )
    {
        this.context = context;
    }

    public Properties load( final String name )
    {
        final InputStream in = findFile( name );
        return loadProperties( in );
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

    private Properties loadProperties( final InputStream in )
    {
        try
        {
            final Properties props = new Properties();
            props.load( in );
            return props;
        }
        catch ( final Exception e )
        {
            throw Exceptions.unchecked( e );
        }
        finally
        {
            IOUtils.closeQuietly( in );
        }
    }
}
