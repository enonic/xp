package com.enonic.wem.api.content.datatype;


import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.content.data.Value;

public class Blob
    extends BaseDataType
{
    Blob( int key )
    {
        super( key, JavaType.BLOB );
    }

    @Override
    public Value ensureTypeOfValue( final Value value )
        throws InconvertibleValueException
    {
        if ( hasCorrectType( value ) )
        {
            return value;
        }
        else if ( value.isJavaType( String.class ) )
        {
            return Value.newValue().type( this ).value( new BlobKey( (String) value.getObject() ) ).build();
        }
        else
        {
            throw new InconvertibleValueException( value, this );
        }
    }

    @Override
    public boolean hasCorrectType( final Object value )
    {
        return byte[].class.isInstance( value ) || BlobKey.class.isInstance( value ) || super.hasCorrectType( value );
    }
}
