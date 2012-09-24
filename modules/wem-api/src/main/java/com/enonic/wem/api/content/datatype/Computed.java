package com.enonic.wem.api.content.datatype;


public class Computed
    extends BaseDataType
{
    Computed( int key )
    {
        super( key, JavaType.STRING );
    }

    @Override
    public String getIndexableString( final Object value )
    {
        return null; // TODO
    }
}
