package com.enonic.xp.launcher.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.felix.utils.properties.InterpolationHelper;

import com.google.common.base.Strings;

public final class ConfigProperties
    extends HashMap<String, String>
{
    @Override
    public String put( final String key, final String value )
    {
        return super.put( key.trim(), value.trim() );
    }

    @Override
    public void putAll( final Map<? extends String, ? extends String> map )
    {
        for ( final Entry<? extends String, ? extends String> entry : map.entrySet() )
        {
            put( entry.getKey(), entry.getValue() );
        }
    }

    public void interpolate()
    {
        InterpolationHelper.performSubstitution( this );
    }

    public boolean getBoolean( final String key )
    {
        final String value = get( key );
        return "true".equals( value );
    }

    public File getFile( final String key )
    {
        final String value = get( key );
        return Strings.isNullOrEmpty( value ) ? null : new File( value );
    }
}
