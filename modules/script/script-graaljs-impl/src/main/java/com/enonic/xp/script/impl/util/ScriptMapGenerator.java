package com.enonic.xp.script.impl.util;

import com.enonic.xp.script.serializer.MapGeneratorBase;

final class ScriptMapGenerator
    extends MapGeneratorBase
{
    private final JavascriptHelper helper;

    ScriptMapGenerator( final JavascriptHelper helper )
    {
        this.helper = helper;
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
        return GraalJSHelper.isNativeObject( value );
    }

    @Override
    protected boolean isArray( final Object value )
    {
        return GraalJSHelper.isNativeArray( value );
    }

    @Override
    protected void putInMap( final Object map, final String key, final Object value )
    {
        if ( value != null )
        {
            GraalJSHelper.addToNativeObject( map, key, value );
        }
    }

    @Override
    protected void addToArray( final Object array, final Object value )
    {
        GraalJSHelper.addToNativeArray( array, value );
    }

    @Override
    protected MapGeneratorBase newGenerator()
    {
        return new ScriptMapGenerator( this.helper );
    }
}
