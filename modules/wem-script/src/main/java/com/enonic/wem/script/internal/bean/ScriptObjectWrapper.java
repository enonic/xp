package com.enonic.wem.script.internal.bean;

import java.util.Map;
import java.util.Set;

import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.ScriptUtils;
import jdk.nashorn.internal.runtime.Undefined;

public final class ScriptObjectWrapper
    extends AbstractJSObject
{
    private final Map<String, Object> map;

    public ScriptObjectWrapper( final Map<String, Object> map )
    {
        this.map = map;
    }

    @Override
    public Object getMember( final String name )
    {
        final Object result = this.map.get( name );
        return toScriptValue( result );
    }

    @SuppressWarnings("unchecked")
    private Object toScriptValue( final Object value )
    {
        if ( value == null )
        {
            return Undefined.getUndefined();
        }

        if ( value instanceof Map )
        {
            return new ScriptObjectWrapper( (Map) value );
        }

        return ScriptUtils.wrap( value );
    }

    @Override
    public boolean hasMember( final String name )
    {
        return this.map.containsKey( name );
    }

    @Override
    public void removeMember( final String name )
    {
        this.map.remove( name );
    }

    @Override
    public void setMember( final String name, final Object value )
    {
        this.map.put( name, value );
    }

    @Override
    public Set<String> keySet()
    {
        return this.map.keySet();
    }
}
