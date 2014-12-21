package com.enonic.wem.script.internal.bean;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.ScriptUtils;
import jdk.nashorn.internal.runtime.Undefined;

import com.enonic.wem.api.convert.Converters;
import com.enonic.wem.script.ScriptValue;

public final class ScriptValueImpl
    implements ScriptValue
{
    private final Object value;

    private final JSObject jsObject;

    private final ScriptMethodInvoker invoker;

    public ScriptValueImpl( final Object value, final ScriptMethodInvoker invoker )
    {
        this.invoker = invoker;

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
    public ScriptValue getMember( final String key )
    {
        if ( !isObject() )
        {
            return null;
        }

        return new ScriptValueImpl( this.jsObject.getMember( key ), this.invoker );
    }

    @Override
    public List<ScriptValue> getArray()
    {
        if ( !isArray() )
        {
            return null;
        }

        return this.jsObject.values().stream().map( this::newScriptObject ).collect( Collectors.toList() );
    }

    private ScriptValue newScriptObject( final Object value )
    {
        return new ScriptValueImpl( value, this.invoker );
    }

    @Override
    public ScriptValue call( final Object... args )
    {
        if ( !isFunction() )
        {
            return null;
        }

        final Object result = this.invoker.invoke( this.jsObject, args );
        return new ScriptValueImpl( result, this.invoker );
    }

    @Override
    public <T> T getValue( final Class<T> type )
    {
        return Converters.convert( this.value, type );
    }
}
