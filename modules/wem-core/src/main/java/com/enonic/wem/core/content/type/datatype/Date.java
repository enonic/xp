package com.enonic.wem.core.content.type.datatype;


import org.joda.time.DateTime;

import com.enonic.wem.core.content.data.Data;

public class Date
    extends AbstractDataType
    implements DataType
{
    public Date()
    {
        super( BasalValueType.DATE );
    }

    public boolean validData( final Data data )
    {
        return data.getValue() instanceof DateTime;
    }
}
