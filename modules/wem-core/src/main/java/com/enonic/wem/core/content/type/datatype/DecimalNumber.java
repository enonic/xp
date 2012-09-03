package com.enonic.wem.core.content.type.datatype;

import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;

public class DecimalNumber
    extends AbstractDataType
{
    DecimalNumber( int key )
    {
        super( key, JavaType.DOUBLE, FieldTypes.WHOLE_NUMBER );
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
