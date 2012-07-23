package com.enonic.wem.core.content.config.field.type.value;


import com.enonic.wem.core.content.FieldValue;

public class MultiLinedString
    extends AbstractBaseValueType
    implements ValueType
{
    public boolean validValue( final FieldValue fieldValue )
    {
        return fieldValue.getValue() instanceof String;
    }
}
