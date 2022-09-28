package com.enonic.xp.script.graal.util;

import java.util.function.Function;

import com.enonic.xp.script.impl.util.JavascriptHelper;
import com.enonic.xp.script.serializer.MapGeneratorBase;

public final class GraalScriptMapGenerator
    extends MapGeneratorBase
{
    private final JavascriptHelper<?> helper;

    public GraalScriptMapGenerator( final JavascriptHelper<?> helper )
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
    protected Object newFunction( final Function<?, ?> function )
    {
        return this.helper.newFunction( function );
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
    protected void putRawValueInMap( final Object map, final String key, final Object value )
    {
        GraalJSHelper.addToNativeObject( map, key, value );
    }

    @Override
    protected void addToArray( final Object array, final Object value )
    {
        GraalJSHelper.addToNativeArray( array, value );
    }

    @Override
    protected MapGeneratorBase newGenerator()
    {
        return new GraalScriptMapGenerator( this.helper );
    }
}
