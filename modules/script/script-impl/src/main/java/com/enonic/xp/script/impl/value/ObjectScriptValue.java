package com.enonic.xp.script.impl.value;

import java.util.Map;
import java.util.Set;

import javax.script.Bindings;

import org.openjdk.nashorn.api.scripting.JSObject;

import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.impl.util.ObjectConverter;

final class ObjectScriptValue
    extends AbstractScriptValue
{
    private final ScriptValueFactory<Bindings> factory;

    private final JSObject value;

    ObjectScriptValue( final ScriptValueFactory<Bindings> factory, final JSObject value )
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
        final ObjectConverter converter = this.factory.getJavascriptHelper().objectConverter();
        return converter.toMap( this.value );
    }
}
