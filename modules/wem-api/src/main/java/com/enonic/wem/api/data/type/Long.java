package com.enonic.wem.api.data.type;


import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;

public class Long
    extends ValueType<java.lang.Long>
{
    Long( int key )
    {
        super( key, JavaTypeConverter.Long.GET );
    }

    @Override
    public Value newValue( final Object value )
    {
        return new Value.Long( convert( value ) );
    }

    @Override
    public Property newProperty( final java.lang.String name, final Value value )
    {
        return new Property.Long( name, value );
    }
}
