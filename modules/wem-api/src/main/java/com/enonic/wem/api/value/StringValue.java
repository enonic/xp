package com.enonic.wem.api.value;

public final class StringValue
    extends Value<String>
{
    public StringValue( final String object )
    {
        super( ValueType.STRING, object );
    }

    @Override
    public final String asString()
    {
        return this.object;
    }
}
