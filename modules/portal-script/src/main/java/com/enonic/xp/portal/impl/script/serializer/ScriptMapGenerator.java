package com.enonic.xp.portal.impl.script.serializer;

import jdk.nashorn.internal.objects.Global;
import jdk.nashorn.internal.objects.NativeArray;
import jdk.nashorn.internal.runtime.ScriptObject;

import com.enonic.xp.portal.script.serializer.MapGeneratorBase;

public final class ScriptMapGenerator
    extends MapGeneratorBase
{
    @Override
    protected Object newMap()
    {
        return Global.newEmptyInstance();
    }

    @Override
    protected Object newArray()
    {
        return Global.allocate( new Object[0] );
    }

    @Override
    protected boolean isMap( final Object value )
    {
        return !isArray( value );
    }

    @Override
    protected boolean isArray( final Object value )
    {
        return ( (ScriptObject) value ).isArray();
    }

    @Override
    protected void putInMap( final Object map, final String key, final Object value )
    {
        ( (ScriptObject) map ).put( key, value, false );
    }

    @Override
    protected void addToArray( final Object array, final Object value )
    {
        NativeArray.push( array, value );
    }
}
