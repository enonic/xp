package com.enonic.xp.script.graaljs.impl.value;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyArray;
import org.graalvm.polyglot.proxy.ProxyObject;

import com.enonic.xp.script.ScriptValue;

public final class ObjectScriptValue
    extends AbstractScriptValue
{
    private final Object value;

    public ObjectScriptValue( final Object value )
    {
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
        if ( value instanceof ProxyObject )
        {
            final ProxyArray proxyArray = (ProxyArray) ( (ProxyObject) value ).getMemberKeys();
            final Set<String> result = new LinkedHashSet<>();
            for ( int i = 0; i < proxyArray.getSize(); i++ )
            {
                result.add( proxyArray.get( i ).toString() );
            }
            return result;
        }
        return null;
    }

    @Override
    public boolean hasMember( final String key )
    {
        if ( value instanceof ProxyObject )
        {
            return ( (ProxyObject) value ).hasMember( key );
        }
        return false;
    }

    @Override
    public ScriptValue getMember( final String key )
    {
        if ( value instanceof ProxyObject )
        {
            return new FunctionScriptValue( Value.asValue( ( (ProxyObject) value ).getMember( key ) ) );
        }
        return null;
    }

    @Override
    public Map<String, Object> getMap()
    {
        return null;
    }
}
