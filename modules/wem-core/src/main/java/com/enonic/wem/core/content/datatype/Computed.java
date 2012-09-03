package com.enonic.wem.core.content.datatype;


public class Computed
    extends AbstractDataType
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
