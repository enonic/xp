package com.enonic.wem.api.content.data.type;


import com.enonic.wem.api.content.data.Value;

public class BinaryReference
    extends BaseDataType
{
    BinaryReference( int key )
    {
        super( key, JavaType.BLOB_KEY );
    }

    private Value toString( final Value value )
    {
        if ( isValueOfExpectedJavaClass( value ) )
        {
            return value;
        }
        else
        {
            throw new InconvertibleValueException( value, this );
        }
    }
}
