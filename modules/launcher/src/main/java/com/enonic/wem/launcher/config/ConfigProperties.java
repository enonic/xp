package com.enonic.wem.launcher.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.utils.properties.InterpolationHelper;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

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
        super.putAll( Maps.transformValues( map, new TrimFunction() ) );
    }

    public void interpolate()
    {
        InterpolationHelper.performSubstitution( this );
    }

    private final class TrimFunction
        implements Function<String, String>
    {
        @Override
        public String apply( final String input )
        {
            return input != null ? input.trim() : null;
        }
    }
}
