package com.enonic.wem.core.content.type.datatype;


import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;

public class MultiLinedString
    extends AbstractDataType
    implements DataType
{
    public MultiLinedString()
    {
        super( BasalValueType.STRING, FieldTypes.TEXT_AREA );
    }

    public boolean validData( final Data data )
    {
        return data.getValue() instanceof String;
    }
}
