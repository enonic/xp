package com.enonic.xp.script.graaljs.impl.value;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyArray;
import org.graalvm.polyglot.proxy.ProxyObject;

import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.graaljs.impl.util.JsObjectConverter;

public final class ObjectScriptValue
    extends AbstractScriptValue
{
    private final ScriptValueFactory factory;

    private final Object value;

    public ObjectScriptValue( final ScriptValueFactory factory, final Object value )
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
        if ( value instanceof Value )
        {
            return ( (Value) value ).getMemberKeys();
        }
        else if ( value instanceof ProxyObject )
        {
            final ProxyArray proxyArray = (ProxyArray) ( (ProxyObject) value ).getMemberKeys();
            final Set<String> result = new LinkedHashSet<>();
            for ( int i = 0; i < proxyArray.getSize(); i++ )
            {
                result.add( proxyArray.get( i ).toString() );
            }
            return result;
        }
        else
        {
            return null;
        }
    }

    @Override
    public boolean hasMember( final String key )
    {
        if ( value instanceof Value )
        {
            return ( (Value) value ).hasMember( key );
        }
        else if ( value instanceof ProxyObject )
        {
            return ( (ProxyObject) value ).hasMember( key );
        }
        else
        {
            return false;
        }
    }

    @Override
    public ScriptValue getMember( final String key )
    {
        if ( value instanceof Value )
        {
            return new FunctionScriptValue( factory, ( (Value) value ).getMember( key ) );
        }
        else if ( value instanceof ProxyObject )
        {
            return new FunctionScriptValue( factory, Value.asValue( ( (ProxyObject) value ).getMember( key ) ) );
        }
        else
        {
            return null;
        }
    }

    @Override
    public Map<String, Object> getMap()
    {
        return new JsObjectConverter( this.factory.getJavascriptHelper() ).toMap( this.value );
    }
}
