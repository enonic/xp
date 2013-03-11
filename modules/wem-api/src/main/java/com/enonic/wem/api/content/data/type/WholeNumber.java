package com.enonic.wem.api.content.data.type;


import com.enonic.wem.api.content.data.Value;

public class WholeNumber
    extends BaseDataType
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


    private Value toWholeNumber( final Value value )
    {
        if ( hasCorrectType( value ) )
        {
            return value;
        }
        else if ( value.isJavaType( String.class ) )
        {
            try
            {
                return newValue( new Long( (String) value.getObject() ) );
            }
            catch ( NumberFormatException e )
            {
                throw new InconvertibleValueException( value, this, e );
            }
        }
        else if ( value.isJavaType( Integer.class ) )
        {
            return newValue( ( (Integer) value.getObject() ).longValue() );
        }
        else if ( value.isJavaType( Double.class ) )
        {
            return newValue( ( (Double) value.getObject() ).longValue() );
        }
        else if ( value.isJavaType( Float.class ) )
        {
            return newValue( ( (Float) value.getObject() ).longValue() );
        }
        else
        {
            throw new InconvertibleValueException( value, this );
        }
    }
}
