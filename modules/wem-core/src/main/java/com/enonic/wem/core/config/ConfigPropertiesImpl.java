package com.enonic.wem.core.config;

import java.util.HashMap;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

public final class ConfigPropertiesImpl
    extends HashMap<String, String>
    implements ConfigProperties
{
    @Override
    public ConfigProperties getSubConfig( final Predicate<String> predicate )
    {
        final ConfigPropertiesImpl map = new ConfigPropertiesImpl();
        map.putAll( Maps.filterKeys( this, predicate ) );
        return map;
    }
}
