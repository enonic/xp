package com.enonic.wem.core.content.type.valuetype;


import org.joda.time.DateTime;

import com.enonic.wem.core.content.data.Value;

public class Date
    extends BaseValueType
    implements ValueType
{
    public boolean validValue( final Value fieldValue )
    {
        return fieldValue.getValue() instanceof DateTime;
    }
}
