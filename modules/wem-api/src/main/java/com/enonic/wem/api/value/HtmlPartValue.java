package com.enonic.wem.api.value;

public final class HtmlPartValue
    extends Value<String>
{
    public HtmlPartValue( final String object )
    {
        super( ValueType.HTML_PART, object );
    }

    @Override
    public String asString()
    {
        return this.object;
    }
}
