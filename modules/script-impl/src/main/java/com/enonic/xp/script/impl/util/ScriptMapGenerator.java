package com.enonic.xp.script.impl.util;

import com.enonic.xp.script.impl.util.NashornHelper;
import com.enonic.xp.script.serializer.MapGeneratorBase;

public final class ScriptMapGenerator
    extends MapGeneratorBase
{
    @Override
    protected Object newMap()
    {
        return NashornHelper.newNativeObject();
    }

    @Override
    protected Object newArray()
    {
        return NashornHelper.newNativeArray();
    }

    @Override
    protected boolean isMap( final Object value )
    {
        return NashornHelper.isNativeObject( value );
    }

    @Override
    protected boolean isArray( final Object value )
    {
        return NashornHelper.isNativeArray( value );
    }

    @Override
    protected void putInMap( final Object map, final String key, final Object value )
    {
        NashornHelper.addToNativeObject( map, key, value );
    }

    @Override
    protected void addToArray( final Object array, final Object value )
    {
        NashornHelper.addToNativeArray( array, value );
    }
}
