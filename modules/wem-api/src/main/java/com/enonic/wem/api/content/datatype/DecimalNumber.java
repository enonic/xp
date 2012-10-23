package com.enonic.wem.api.content.datatype;

public class DecimalNumber
    extends BaseDataType
{
    DecimalNumber( int key )
    {
        super( key, JavaType.DOUBLE );
    }

    @Override
    public String getIndexableString( final Object value )
    {
        return value.toString();
    }

    @Override
    public Object ensureTypeOfValue( final Object value )
    {
        return toDecimalNumber( value );
    }

    private Double toDecimalNumber( final Object value )
    {
        if ( hasCorrectType( value ) )
        {
            return (Double) value;
        }
        else if ( value instanceof String )
        {
            try
            {
                return new Double( (String) value );
            }
            catch ( NumberFormatException e )
            {
                throw new InconvertibleValueException( value, this, e );
            }
        }
        else if ( value instanceof Integer )
        {
            return ( (Integer) value ).doubleValue();
        }
        else if ( value instanceof Long )
        {
            return ( (Long) value ).doubleValue();
        }
        else if ( value instanceof Float )
        {
            return ( (Float) value ).doubleValue();
        }
        else
        {
            throw new InconvertibleValueException( value, this );
        }
    }
}
