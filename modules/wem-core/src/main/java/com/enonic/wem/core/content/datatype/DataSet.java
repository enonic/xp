package com.enonic.wem.core.content.datatype;


public class DataSet
    extends AbstractDataType
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
