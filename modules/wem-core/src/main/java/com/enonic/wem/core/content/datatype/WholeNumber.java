package com.enonic.wem.core.content.datatype;


public class WholeNumber
    extends AbstractDataType
{
    WholeNumber( int key )
    {
        super( key, JavaType.LONG );
    }

    @Override
    public String getIndexableString( final Object value )
    {
        return value.toString();
    }

    @Override
    public Object ensureType( final Object value )
    {
        return toWholeNumber( value );
    }

    private Long toWholeNumber( final Object value )
    {
        if ( hasCorrectType( value ) )
        {
            return (Long) value;
        }
        else if ( value instanceof String )
        {
            return new Long( (String) value );
        }
        else if ( value instanceof Integer )
        {
            return ( (Integer) value ).longValue();
        }
        else if ( value instanceof Double )
        {
            return ( (Double) value ).longValue();
        }
        else if ( value instanceof Float )
        {
            return ( (Float) value ).longValue();
        }
        else
        {
            throw new InconvertibleException( value, this );
        }
    }
}
