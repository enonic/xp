package com.enonic.wem.api.data.type;


import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;

public class String
    extends ValueType<java.lang.String>
{
    String( int key )
    {
        super( key, JavaTypeConverters.STRING );
    }

    @Override
    public Value newValue( final Object value )
    {
        return Value.newString( convert( value ) );
    }

    @Override
    public Property newProperty( final java.lang.String name, final Value value )
    {
        return new Property.String( name, value );
    }
}
