package com.enonic.wem.script.internal.bean;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.ScriptUtils;
import jdk.nashorn.internal.runtime.Undefined;

import com.enonic.wem.api.convert.Converters;
import com.enonic.wem.script.ScriptObject;
import com.enonic.wem.script.internal.error.ErrorHelper;

public final class ScriptObjectImpl
    implements ScriptObject
{
    private final Object value;

    private final JSObject jsObject;

    public ScriptObjectImpl( final Object value )
    {
        Object unwrapped = ScriptUtils.unwrap( value );
        if ( unwrapped instanceof Undefined )
        {
            unwrapped = null;
        }

        this.jsObject = ( unwrapped instanceof JSObject ) ? (JSObject) unwrapped : null;
        this.value = ( this.jsObject == null ) ? unwrapped : null;
    }

    @Override
    public boolean isArray()
    {
        return ( this.jsObject != null ) && this.jsObject.isArray();
    }

    @Override
    public boolean isObject()
    {
        return ( this.jsObject != null ) && ( !this.jsObject.isFunction() && !this.jsObject.isArray() );
    }

    @Override
    public boolean isValue()
    {
        return this.value != null;
    }

    @Override
    public boolean isFunction()
    {
        return ( this.jsObject != null ) && this.jsObject.isFunction();
    }

    @Override
    public boolean isUndefined()
    {
        return ( this.value == null ) && ( this.jsObject == null );
    }

    @Override
    public Object getValue()
    {
        return this.value;
    }

    @Override
    public Set<String> getKeys()
    {
        if ( !isObject() )
        {
            return null;
        }

        return this.jsObject.keySet();
    }

    @Override
    public boolean hasMember( final String key )
    {
        return isObject() && this.jsObject.hasMember( key );
    }

    @Override
    public ScriptObject getMember( final String key )
    {
        if ( !isObject() )
        {
            return null;
        }

        return new ScriptObjectImpl( this.jsObject.getMember( key ) );
    }

    @Override
    public List<ScriptObject> getArray()
    {
        if ( !isArray() )
        {
            return null;
        }

        return this.jsObject.values().stream().map( this::newScriptObject ).collect( Collectors.toList() );
    }

    private ScriptObject newScriptObject( final Object value )
    {
        return new ScriptObjectImpl( value );
    }

    @Override
    public ScriptObject call( final Object... args )
    {
        if ( !isFunction() )
        {
            return null;
        }

        try
        {
            final Object result = this.jsObject.call( this.jsObject, JsObjectConverter.toJsArray( args ) );
            return new ScriptObjectImpl( result );
        }
        catch ( final Exception e )
        {
            throw ErrorHelper.handleError( e );
        }
    }

    @Override
    public <T> T getValue( final Class<T> type )
    {
        return Converters.convert( this.value, type );
    }
}
