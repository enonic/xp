package com.enonic.wem.core.content.type.datatype;


import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;

public class MultiLinedString
    extends AbstractDataType
{
    MultiLinedString( int key )
    {
        super( key, JavaType.STRING, FieldTypes.TEXT_AREA );
    }

    @Override
    public String getIndexableString( final Object value )
    {
        return value.toString();
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
            return (String) value;
        }
        else
        {
            throw new InconvertibleException( value, this );
        }
    }
}
