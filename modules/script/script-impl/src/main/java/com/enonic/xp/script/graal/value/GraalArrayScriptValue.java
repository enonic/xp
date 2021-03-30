package com.enonic.xp.script.graal.value;

import java.util.ArrayList;
import java.util.List;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.impl.value.AbstractScriptValue;
import com.enonic.xp.script.impl.value.ScriptValueFactory;

final class GraalArrayScriptValue
    extends AbstractScriptValue
{
    private final Context context;

    private final ScriptValueFactory factory;

    private final Value value;

    GraalArrayScriptValue( final Context context, final ScriptValueFactory factory, final Value value )
    {
        this.context = context;
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
        synchronized ( context )
        {
            final List<ScriptValue> result = new ArrayList<>();
            for ( int i = 0; i < this.value.getArraySize(); i++ )
            {
                final Value arrayElement = this.value.getArrayElement( i );
                final ScriptValue wrapped = this.factory.newValue( arrayElement );
                if ( wrapped != null )
                {
                    result.add( wrapped );
                }
            }
            return result;
        }
    }

    @Override
    public <T> List<T> getArray( final Class<T> type )
    {
        synchronized ( context )
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
    }

    @Override
    public List<Object> getList()
    {
        synchronized ( context )
        {
            final List<Object> result = new ArrayList<>();

            for ( int i = 0; i < this.value.getArraySize(); i++ )
            {
                Value arrayElement = this.value.getArrayElement( i );

                final ScriptValue item = this.factory.newValue( arrayElement );
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
}
