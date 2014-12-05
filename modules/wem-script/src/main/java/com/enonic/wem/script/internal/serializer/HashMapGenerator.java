package com.enonic.wem.script.internal.serializer;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public final class HashMapGenerator
    extends MapGeneratorBase
{
    @Override
    protected Object newMap()
    {
        return Maps.newHashMap();
    }

    @Override
    protected Object newArray()
    {
        return Lists.newArrayList();
    }

    @Override
    protected boolean isMap( final Object value )
    {
        return ( value instanceof Map );
    }

    @Override
    protected boolean isArray( final Object value )
    {
        return ( value instanceof List );
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void putInMap( final Object map, final String key, final Object value )
    {
        ( (Map) map ).put( key, value );
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void addToArray( final Object array, final Object value )
    {
        ( (List) array ).add( value );
    }
}
