package com.enonic.wem.itest.config;

import java.util.Map;
import java.util.Properties;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

public final class ConfigProperties
    extends Properties
{
    public Map<String, String> getMap()
    {
        return Maps.fromProperties( this );
    }

    public Map<String, String> getSubMap( final Predicate<String> predicate )
    {
        return Maps.filterKeys( getMap(), predicate );
    }

}
