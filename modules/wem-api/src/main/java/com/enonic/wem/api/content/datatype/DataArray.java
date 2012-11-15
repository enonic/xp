package com.enonic.wem.api.content.datatype;


public class DataArray
    extends BaseDataType
{
    DataArray( int key )
    {
        super( key, JavaType.DATA_ARRAY );
    }

    @Override
    public String getIndexableString( final Object value )
    {
        return null; // TODO
    }
}
