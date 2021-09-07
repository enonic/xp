package com.enonic.xp.script.impl.value;

import org.graalvm.polyglot.Context;

import com.enonic.xp.convert.Converters;

final class ScalarScriptValue
    extends AbstractScriptValue
{
    private final Context context;

    private final Object value;

    ScalarScriptValue( final Context context, final Object value )
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
