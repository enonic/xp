package com.enonic.wem.api.value;

public final class XmlValue
    extends Value<String>
{
    public XmlValue( final String object )
    {
        super( ValueType.XML, object );
    }

    @Override
    public final String asString()
    {
        return this.object;
    }
}
