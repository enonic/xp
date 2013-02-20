package com.enonic.wem.api.content.data.datatype;


public class HtmlPart
    extends BaseDataType
    implements DataType
{
    HtmlPart( int key )
    {
        super( key, JavaType.STRING );
    }

    @Override
    public String getIndexableString( final Object value )
    {
        return value.toString();
    }
}
