package com.enonic.wem.api.data.type;

import java.lang.String;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;

public class Boolean
    extends ValueType<java.lang.Boolean>
{
    Boolean( int key )
    {
        super( key, JavaTypeConverters.BOOLEAN );
    }

    @Override
    public Value newValue( final Object value )
    {
        return new Value.Boolean( convert( value ) );
    }

    @Override
    public Property newProperty( final String name, final Value value )
    {
        return new Property.Boolean( name, value );
    }
}
