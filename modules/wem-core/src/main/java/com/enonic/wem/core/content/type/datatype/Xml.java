package com.enonic.wem.core.content.type.datatype;


import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;

public class Xml
    extends AbstractDataType
    implements DataType
{
    public Xml()
    {
        super( BasalValueType.STRING, FieldTypes.XML );
    }

    public boolean validData( final Data data )
    {
        return true;
    }
}
