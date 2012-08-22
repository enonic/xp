package com.enonic.wem.core.content.type.datatype;


import com.enonic.wem.core.content.data.Data;

public class Xml
    extends AbstractDataType
    implements DataType
{
    public Xml()
    {
        super( BasalValueType.STRING );
    }

    public boolean validData( final Data data )
    {
        return true;
    }
}
