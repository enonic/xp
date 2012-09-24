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
    public Object ensureType( final Object value )
    {
        return toDecimalNumber( value );
    }

    @Override
    public boolean isConvertibleTo( final JavaType type )
    {
        return type == JavaType.DOUBLE || type == JavaType.STRING;
    }

    @Override
    public Double convertToDouble( final Object value )
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
                throw new InconvertibleException( value, this, e );
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
            throw new InconvertibleException( value, this );
        }
    }
}
