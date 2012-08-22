package com.enonic.wem.core.content.type.datatype;


import com.enonic.wem.core.content.data.Data;

public class MultiLinedString
    extends AbstractDataType
    implements DataType
{
    public MultiLinedString()
    {
        super( BasalValueType.STRING );
    }

    public boolean validData( final Data data )
    {
        return data.getValue() instanceof String;
    }
}
