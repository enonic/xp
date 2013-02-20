package com.enonic.wem.api.content.data.type;

import com.enonic.wem.api.content.data.Value;

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
    public Value ensureTypeOfValue( final Value value )
        throws InconvertibleValueException
    {
        return toDecimalNumber( value );
    }

    private Value toDecimalNumber( final Value value )
    {
        if ( hasCorrectType( value ) )
        {
            return value;
        }
        else if ( value.isJavaType( String.class ) )
        {
            try
            {
                return newValue( new Double( (String) value.getObject() ) );
            }
            catch ( NumberFormatException e )
            {
                throw new InconvertibleValueException( value, this, e );
            }
        }
        else if ( value.isJavaType( Integer.class ) )
        {
            return newValue( ( (Integer) value.getObject() ).doubleValue() );
        }
        else if ( value.isJavaType( Long.class ) )
        {
            return newValue( ( (Long) value.getObject() ).doubleValue() );
        }
        else if ( value.isJavaType( Float.class ) )
        {
            return newValue( ( (Float) value.getObject() ).doubleValue() );
        }
        else
        {
            throw new InconvertibleValueException( value, this );
        }
    }

}
