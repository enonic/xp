package com.enonic.wem.api.content.data.type;


import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.Value;

public class Xml
    extends ValueType
{
    Xml( int key )
    {
        super( key, JavaTypeConverters.STRING_CONVERTER );
    }

    @Override
    public Value newValue( final Object value )
    {
        return new Value.Xml( JavaTypeConverters.STRING_CONVERTER.convertFrom( value ) );
    }

    @Override
    public Property newProperty( final String name, final Value value )
    {
        return new Property.Xml( name, value );
    }
}
