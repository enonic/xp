package com.enonic.xp.launcher.impl.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.utils.properties.InterpolationHelper;

public final class ConfigProperties
    extends HashMap<String, String>
{
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
}
