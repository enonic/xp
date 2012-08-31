package com.enonic.wem.core.content.type.datatype;


import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;

public class HtmlPart
    extends AbstractDataType
    implements DataType
{
    public HtmlPart()
    {
        super( BasalValueType.STRING, FieldTypes.HTML_AREA );
    }

    @Override
    public boolean validData( final Data data )
    {
        return true;
    }
}
