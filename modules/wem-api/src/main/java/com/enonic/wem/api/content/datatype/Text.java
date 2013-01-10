package com.enonic.wem.api.content.datatype;


import com.enonic.wem.api.content.data.Value;

public class Text
    extends BaseDataType
{
    Text( int key )
    {
        super( key, JavaType.STRING );
    }

    @Override
    public String getIndexableString( final Object value )
    {
        return value.toString();
    }

    @Override
    public Value ensureTypeOfValue( final Value value )
        throws InconvertibleValueException
    {
        return toString( value );
    }

    public Value toString( final Value value )
    {
        if ( hasCorrectType( value ) )
        {
            return value;
        }
        else
        {
            throw new InconvertibleValueException( value, this );
        }
    }
}
