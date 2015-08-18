package com.enonic.xp.script.impl.value;

import java.util.Map;
import java.util.Set;

import jdk.nashorn.api.scripting.JSObject;

import com.enonic.xp.script.ScriptValue;

final class ObjectScriptValue
    extends AbstractScriptValue
{
    private final ScriptValueFactory factory;

    private final JSObject value;

    public ObjectScriptValue( final ScriptValueFactory factory, final JSObject value )
    {
        this.factory = factory;
        this.value = value;
    }

    @Override
    public boolean isObject()
    {
        return true;
    }

    @Override
    public Set<String> getKeys()
    {
        return this.value.keySet();
    }

    @Override
    public boolean hasMember( final String key )
    {
        return this.value.hasMember( key );
    }

    @Override
    public ScriptValue getMember( final String key )
    {
        return this.factory.newValue( this.value.getMember( key ) );
    }

    @Override
    public Map<String, Object> getMap()
    {
        return JsObjectConverter.toMap( this.value );
    }
}
