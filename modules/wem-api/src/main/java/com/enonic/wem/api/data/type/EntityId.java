package com.enonic.wem.api.data.type;

import java.lang.String;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;

public class EntityId
    extends ValueType<com.enonic.wem.api.entity.EntityId>
{

    EntityId( int key )
    {
        super( key, JavaTypeConverter.EntityId.GET );
    }

    @Override
    public Value newValue( final Object value )
    {
        return new Value.EntityId( convert( value ) );
    }

    @Override
    public Property newProperty( final String name, final Value value )
    {
        return new Property.EntityId( name, value );
    }
}
