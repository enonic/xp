package com.enonic.wem.core.content.type.valuetype;


import com.enonic.wem.core.content.data.Value;

public class HtmlPart
    extends BaseValueType
    implements ValueType
{
    @Override
    public boolean validValue( final Value fieldValue )
    {
        return true;
    }
}
