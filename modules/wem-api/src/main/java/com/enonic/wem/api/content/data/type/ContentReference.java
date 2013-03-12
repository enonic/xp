package com.enonic.wem.api.content.data.type;

import com.enonic.wem.api.content.data.Value;

public class ContentReference
    extends BaseDataType
{
    ContentReference( int key )
    {
        super( key, JavaType.STRING );
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
