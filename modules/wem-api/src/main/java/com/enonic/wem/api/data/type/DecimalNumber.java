package com.enonic.wem.api.data.type;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;

public class DecimalNumber
    extends ValueType<Double>
{
    DecimalNumber( int key )
    {
        super( key, JavaTypeConverter.Double.GET );
    }

    @Override
    public Value newValue( final Object value )
    {
        return new Value.DecimalNumber( convert( value ) );
    }

    @Override
    public Property newProperty( final String name, final Value value )
    {
        return new Property.DecimalNumber( name, value );
    }
}
