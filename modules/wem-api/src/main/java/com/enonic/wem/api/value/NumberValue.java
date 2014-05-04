package com.enonic.wem.api.value;

public abstract class NumberValue<T extends Number>
    extends Value<T>
{
    public NumberValue( final ValueType type, final T object )
    {
        super( type, object );
    }

    @Override
    public final String asString()
    {
        return this.object.toString();
    }
}
