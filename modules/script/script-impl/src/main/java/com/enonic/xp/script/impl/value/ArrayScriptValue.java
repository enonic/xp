package com.enonic.xp.script.impl.value;

import java.util.ArrayList;
import java.util.List;

import jdk.nashorn.api.scripting.JSObject;

import com.enonic.xp.script.ScriptValue;

final class ArrayScriptValue
    extends AbstractScriptValue
{
    private final ScriptValueFactory<?> factory;

    private final JSObject value;

    ArrayScriptValue( final ScriptValueFactory<?> factory, final JSObject value )
    {
        this.factory = factory;
        this.value = value;
    }

    @Override
    public boolean isArray()
    {
        return true;
    }

    @Override
    public List<ScriptValue> getArray()
    {
        final List<ScriptValue> result = new ArrayList<>();
        for ( final Object item : this.value.values() )
        {
            final ScriptValue wrapped = this.factory.newValue( item );
            if ( wrapped != null )
            {
                result.add( wrapped );
            }
        }

        return result;
    }

    @Override
    public <T> List<T> getArray( final Class<T> type )
    {
        final List<T> result = new ArrayList<>();
        for ( final ScriptValue item : getArray() )
        {
            final T converted = item.getValue( type );
            if ( converted != null )
            {
                result.add( converted );
            }
        }

        return result;
    }

    @Override
    public List<Object> getList()
    {
        final List<Object> result = new ArrayList<>();
        for ( final Object val : this.value.values() )
        {
            final ScriptValue item = this.factory.newValue( val );
            if ( item == null )
            {
                result.add( null );
            }
            else if ( item.isValue() )
            {
                final Object converted = item.getValue( Object.class );
                result.add( converted );
            }
            else if ( item.isObject() )
            {
                final Object obj = item.getMap();
                if ( obj != null )
                {
                    result.add( obj );
                }
            }
            else if ( item.isArray() )
            {
                final Object obj = item.getList();
                if ( obj != null )
                {
                    result.add( obj );
                }
            }
        }
        return result;
    }
}
