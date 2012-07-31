package com.enonic.wem.core.content.type.valuetype;


import org.joda.time.DateTime;

import com.enonic.wem.core.content.data.Value;

public class Date
    extends AbstractValueType
    implements ValueType
{
    public Date()
    {
        super( BasalValueType.DATE );
    }

    public boolean validValue( final Value fieldValue )
    {
        return fieldValue.getValue() instanceof DateTime;
    }
}
