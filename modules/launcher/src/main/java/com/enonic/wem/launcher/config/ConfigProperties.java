package com.enonic.wem.launcher.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.lang.text.StrSubstitutor;

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

    private ConfigProperties transform( final Function<String, String> function )
    {
        final ConfigProperties props = new ConfigProperties();
        props.putAll( Maps.transformValues( this, function ) );
        return props;
    }

    public ConfigProperties interpolate()
    {
        final StrLookup lookup = StrLookup.mapLookup( this );
        final StrSubstitutor substitutor = new StrSubstitutor( lookup );

        return transform( new Function<String, String>()
        {
            @Override
            public String apply( final String input )
            {
                return substitutor.replace( input );
            }
        } );
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
