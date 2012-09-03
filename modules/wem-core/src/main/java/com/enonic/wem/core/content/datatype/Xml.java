package com.enonic.wem.core.content.datatype;


public class Xml
    extends AbstractDataType
{
    Xml( int key )
    {
        super( key, JavaType.STRING );
    }

    @Override
    public String getIndexableString( final Object value )
    {
        return value.toString();
    }

    @Override
    public Object ensureType( final Object value )
    {
        return toXml( value );
    }

    public String toXml( final Object value )
    {
        if ( hasCorrectType( value ) )
        {
            return (String) value;
        }
        else
        {
            throw new InconvertibleException( value, this );
        }
    }
}
