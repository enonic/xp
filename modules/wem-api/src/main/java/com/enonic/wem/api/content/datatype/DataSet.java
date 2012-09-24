package com.enonic.wem.api.content.datatype;


public class DataSet
    extends BaseDataType
{
    DataSet( int key )
    {
        super( key, JavaType.DATA_SET );
    }

    @Override
    public String getIndexableString( final Object value )
    {
        return null; // TODO
    }
}
