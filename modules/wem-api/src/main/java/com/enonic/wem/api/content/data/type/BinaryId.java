package com.enonic.wem.api.content.data.type;


import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.Value;

public class BinaryId
    extends BasePropertyType
{
    BinaryId( int key )
    {
        super( key, JavaType.BINARY_ID );
    }

    @Override
    public Value newValue( final Object value )
    {
        return new Value.BinaryId( JavaType.BINARY_ID.convertFrom( value ) );
    }

    @Override
    public Value.AbstractValueBuilder<Value.BinaryId, com.enonic.wem.api.content.binary.BinaryId> newValueBuilder()
    {
        return new Value.BinaryId.ValueBuilder();
    }

    @Override
    public Property newProperty( final String name, final Value value )
    {
        return new Property.BinaryId( name, value );
    }
}
