package com.enonic.xp.script.graal.value;

import org.graalvm.polyglot.Context;

import com.enonic.xp.convert.Converters;
import com.enonic.xp.script.impl.value.AbstractScriptValue;

final class GraalScalarScriptValue
    extends AbstractScriptValue
{
    private final Context context;

    private final Object value;

    GraalScalarScriptValue( final Context context, final Object value )
    {
        this.context = context;
        this.value = value;
    }

    @Override
    public boolean isValue()
    {
        return true;
    }

    @Override
    public Object getValue()
    {
        return value;
    }

    @Override
    public <T> T getValue( final Class<T> type )
    {
        synchronized ( context )
        {
            return Converters.convert( this.value, type );
        }
    }
}
