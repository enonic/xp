package com.enonic.xp.script.graal.value;

import java.util.Map;
import java.util.Set;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.impl.util.ObjectConverter;
import com.enonic.xp.script.impl.value.AbstractScriptValue;
import com.enonic.xp.script.impl.value.ScriptValueFactory;

final class GraalObjectScriptValue
    extends AbstractScriptValue
{
    private final Context context;

    private final ScriptValueFactory factory;

    private final Value value;

    GraalObjectScriptValue( final Context context, final ScriptValueFactory factory, final Value value )
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
            final ObjectConverter converter = this.factory.getJavascriptHelper().objectConverter();
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
