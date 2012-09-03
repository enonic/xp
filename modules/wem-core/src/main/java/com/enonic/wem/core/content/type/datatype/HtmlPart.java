package com.enonic.wem.core.content.type.datatype;


import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;

public class HtmlPart
    extends AbstractDataType
    implements DataType
{
    HtmlPart( int key )
    {
        super( key, JavaType.STRING, FieldTypes.HTML_AREA );
    }

    @Override
    public String getIndexableString( final Object value )
    {
        return value.toString();
    }
}
