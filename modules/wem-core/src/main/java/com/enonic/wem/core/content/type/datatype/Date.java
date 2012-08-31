package com.enonic.wem.core.content.type.datatype;


import org.joda.time.DateTime;

import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;

public class Date
    extends AbstractDataType
    implements DataType
{
    public Date()
    {
        super( BasalValueType.DATE, FieldTypes.DATE );
    }

    public boolean validData( final Data data )
    {
        return data.getValue() instanceof DateTime;
    }
}
