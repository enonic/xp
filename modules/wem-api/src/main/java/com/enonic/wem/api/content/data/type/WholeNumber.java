package com.enonic.wem.api.content.data.type;


import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.Value;

public class WholeNumber
    extends ValueType
{
    WholeNumber( int key )
    {
        super( key, JavaTypeConverters.LONG_CONVERTER );
    }

    @Override
    public Value newValue( final Object value )
    {
        return new Value.WholeNumber( JavaTypeConverters.LONG_CONVERTER.convertFrom( value ) );
    }

    @Override
    public Property newProperty( final String name, final Value value )
    {
        return new Property.WholeNumber( name, value );
    }
}
