package com.enonic.xp.script.impl.value;

import java.util.Map;
import java.util.Set;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.impl.util.JsObjectConverter;

final class ObjectScriptValue
    extends AbstractScriptValue
{
    private final Context context;

    private final ScriptValueFactory factory;

    private final Value value;

    public ObjectScriptValue( final Context context, final ScriptValueFactory factory, final Value value )
    {
        this.context = context;
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
        synchronized ( context )
        {
            return value.getMemberKeys();
        }
    }

    @Override
    public boolean hasMember( final String key )
    {
        synchronized ( context )
        {
            return value.hasMember( key );
        }
    }

    @Override
    public ScriptValue getMember( final String key )
    {
        synchronized ( context )
        {
            return factory.newValue( value.getMember( key ) );
        }
    }

    @Override
    public Map<String, Object> getMap()
    {
        synchronized ( context )
        {
            final JsObjectConverter converter = new JsObjectConverter( this.factory.getJavascriptHelper() );
            return converter.toMap( this.value );
        }
    }

    @Override
    public Object getValue()
    {
        synchronized ( context )
        {
            return value.isHostObject() ? value.asHostObject() : value;
        }
    }
}
