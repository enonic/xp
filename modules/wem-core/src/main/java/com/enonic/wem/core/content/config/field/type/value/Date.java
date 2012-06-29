package com.enonic.wem.core.content.config.field.type.value;


import org.joda.time.DateTime;

import com.enonic.wem.core.content.FieldValue;

public class Date
    implements ValueType
{
    public boolean validValue( final FieldValue fieldValue )
    {
        return fieldValue.getValue() instanceof DateTime;
    }
}
