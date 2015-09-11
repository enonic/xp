package com.enonic.xp.config;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import com.google.common.base.Strings;

public final class ConfigurationLoader
{
    private final Class context;

    public ConfigurationLoader( final Class context )
    {
        this.context = context;
    }

    public Configuration load( final String name )
        throws Exception
    {
        final InputStream in = findFile( name );
        final Properties props = loadProperties( in );
        return build( props );
    }

    private InputStream findFile( final String name )
        throws Exception
    {
        final InputStream in = this.context.getResourceAsStream( name );
        if ( in == null )
        {
            throw new FileNotFoundException( "Failed to find resource [" + name + "]" );
        }

        return in;
    }

    private Properties loadProperties( final InputStream in )
        throws Exception
    {
        try
        {
            final Properties props = new Properties();
            props.load( in );
            return props;
        }
        finally
        {
            in.close();
        }
    }

    private Configuration build( final Properties props )
    {
        final Configuration.Builder builder = Configuration.create();
        for ( final Object key : props.keySet() )
        {
            final String strKey = key.toString();
            final String value = Strings.nullToEmpty( props.getProperty( strKey ) ).trim();
            builder.add( strKey.trim(), value );
        }

        return builder.build();
    }
}
