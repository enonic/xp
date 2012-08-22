package com.enonic.wem.core.content.type.datatype;


import com.enonic.wem.core.content.data.Data;

public class HtmlPart
    extends AbstractDataType
    implements DataType
{
    public HtmlPart()
    {
        super( BasalValueType.STRING );
    }

    @Override
    public boolean validData( final Data data )
    {
        return true;
    }
}
