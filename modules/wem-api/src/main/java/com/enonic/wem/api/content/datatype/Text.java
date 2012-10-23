package com.enonic.wem.api.content.datatype;


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
    public Object ensureTypeOfValue( final Object value )
    {
        return toString( value );
    }

    public String toString( final Object value )
    {
        if ( hasCorrectType( value ) )
        {
            return (java.lang.String) value;
        }
        else
        {
            throw new InconvertibleValueException( value, this );
        }
    }
}
