package com.enonic.wem.core.content.datatype;


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
    public boolean isConvertibleTo( final JavaType type )
    {
        return type == JavaType.STRING;
    }

    @Override
    public String convertToString( final Object value )
    {
        return (String) value;
    }

    @Override
    public Double convertToDouble( final Object value )
    {
        return new Double( (String) value );
    }

    @Override
    public Object ensureType( final Object value )
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
            throw new InconvertibleException( value, this );
        }
    }
}
