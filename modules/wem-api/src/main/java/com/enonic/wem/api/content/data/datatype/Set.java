package com.enonic.wem.api.content.data.datatype;


public class Set
    extends BaseDataType
{
    Set( int key )
    {
        super( key, JavaType.DATA_SET );
    }

    @Override
    public String getIndexableString( final Object value )
    {
        return null; // TODO
    }
}
