package com.enonic.wem.core.content.datatype;


import com.enonic.cms.framework.blob.BlobKey;

public class Blob
    extends BaseDataType
{
    Blob( int key )
    {
        super( key, JavaType.BLOB );
    }

    @Override
    public Object ensureType( final Object value )
    {
        if ( hasCorrectType( value ) )
        {
            return value;
        }
        else if ( value instanceof String )
        {
            return new BlobKey( (String) value );
        }
        else
        {
            throw new InconvertibleException( value, this );
        }
    }

    @Override
    boolean hasCorrectType( final Object value )
    {
        return byte[].class.isInstance( value ) || super.hasCorrectType( value );
    }
}
