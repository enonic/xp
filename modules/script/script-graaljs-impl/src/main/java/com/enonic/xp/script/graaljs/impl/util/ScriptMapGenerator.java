package com.enonic.xp.script.graaljs.impl.util;

import com.enonic.xp.script.serializer.MapGeneratorBase;

public class ScriptMapGenerator
    extends MapGeneratorBase
{
    @Override
    protected Object newMap()
    {
        return null;
    }

    @Override
    protected Object newArray()
    {
        return null;
    }

    @Override
    protected boolean isMap( final Object value )
    {
        return false;
    }

    @Override
    protected boolean isArray( final Object value )
    {
        return false;
    }

    @Override
    protected void putInMap( final Object map, final String key, final Object value )
    {

    }

    @Override
    protected void addToArray( final Object array, final Object value )
    {

    }

    @Override
    protected MapGeneratorBase newGenerator()
    {
        return new ScriptMapGenerator();
    }
}
