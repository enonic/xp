package com.enonic.xp.portal.impl.script.bean;

import com.enonic.wem.api.convert.Converters;

final class ScalarScriptValue
    extends AbstractScriptValue
{
    private final Object value;

    public ScalarScriptValue( final Object value )
    {
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
        return this.value;
    }

    @Override
    public <T> T getValue( final Class<T> type )
    {
        return Converters.convert( this.value, type );
    }
}
