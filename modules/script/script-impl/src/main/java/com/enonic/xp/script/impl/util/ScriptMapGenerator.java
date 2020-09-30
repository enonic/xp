package com.enonic.xp.script.impl.util;

import com.enonic.xp.script.serializer.MapGeneratorBase;

final class ScriptMapGenerator
    extends MapGeneratorBase
{
    private final JavascriptHelper helper;

    private final Boolean includeNullValues;

    ScriptMapGenerator( final JavascriptHelper helper )
    {
        this( helper, false );
    }

    ScriptMapGenerator( final JavascriptHelper helper, final Boolean includeNullValues )
    {
        this.helper = helper;
        this.includeNullValues = includeNullValues != null ? includeNullValues : false;

        initRoot();
    }

    @Override
    protected Object newMap()
    {
        return this.helper.newJsObject();
    }

    @Override
    protected Object newArray()
    {
        return this.helper.newJsArray();
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
        if ( includeNullValues )
        {
            NashornHelper.addToNativeObject( map, key, value );
        }
        else if ( value != null )
        {
            NashornHelper.addToNativeObject( map, key, value );
        }
    }

    @Override
    protected void addToArray( final Object array, final Object value )
    {
        NashornHelper.addToNativeArray( array, value );
    }

    @Override
    protected MapGeneratorBase newGenerator()
    {
        return new ScriptMapGenerator( this.helper );
    }
}
