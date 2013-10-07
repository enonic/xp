package com.enonic.wem.api.data.type;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;

public class Double
    extends ValueType<java.lang.Double>
{
    Double( int key )
    {
        super( key, JavaTypeConverter.Double.GET );
    }

    @Override
    public Value newValue( final Object value )
    {
        return new Value.Double( convert( value ) );
    }

    @Override
    public Property newProperty( final String name, final Value value )
    {
        return new Property.Double( name, value );
    }
}
