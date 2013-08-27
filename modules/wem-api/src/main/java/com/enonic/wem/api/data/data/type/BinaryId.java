package com.enonic.wem.api.data.data.type;


import com.enonic.wem.api.data.data.Property;
import com.enonic.wem.api.data.data.Value;

public class BinaryId
    extends ValueType<com.enonic.wem.api.content.binary.BinaryId>
{
    BinaryId( int key )
    {
        super( key, JavaTypeConverter.BinaryId.GET );
    }

    @Override
    public Value newValue( final Object value )
    {
        return new Value.BinaryId( convert( value ) );
    }

    @Override
    public Property newProperty( final String name, final Value value )
    {
        return new Property.BinaryId( name, value );
    }
}
