package com.enonic.wem.api.content.data.type;


import com.enonic.wem.api.content.data.Value;

public class BinaryId
    extends BaseDataType
{
    BinaryId( int key )
    {
        super( key, JavaType.BINARY_ID );
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
