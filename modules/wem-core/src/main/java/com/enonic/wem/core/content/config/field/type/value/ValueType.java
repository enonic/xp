package com.enonic.wem.core.content.config.field.type.value;


import com.enonic.wem.core.content.FieldValue;

public interface ValueType
{
    public boolean validValue( final FieldValue fieldValue );
}
