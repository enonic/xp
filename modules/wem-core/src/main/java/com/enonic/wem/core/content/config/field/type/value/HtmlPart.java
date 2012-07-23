package com.enonic.wem.core.content.config.field.type.value;


import com.enonic.wem.core.content.FieldValue;

public class HtmlPart
    extends AbstractBaseValueType
    implements ValueType
{
    @Override
    public boolean validValue( final FieldValue fieldValue )
    {
        return true;
    }
}
