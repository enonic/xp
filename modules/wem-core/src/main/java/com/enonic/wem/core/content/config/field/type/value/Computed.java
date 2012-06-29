package com.enonic.wem.core.content.config.field.type.value;


import com.enonic.wem.core.content.FieldValue;

public class Computed
    implements ValueType
{
    public boolean validValue( final FieldValue fieldValue )
    {
        return true;
    }
}
