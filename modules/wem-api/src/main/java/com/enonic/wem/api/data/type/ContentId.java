package com.enonic.wem.api.data.type;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;

public class ContentId
    extends ValueType<com.enonic.wem.api.content.ContentId>
{
    ContentId( int key )
    {
        super( key, JavaTypeConverter.ContentId.GET );
    }

    @Override
    public Value newValue( final Object value )
    {
        return new Value.ContentId( convert( value ) );
    }

    @Override
    public Property newProperty( final String name, final Value value )
    {
        return new Property.ContentId( name, value );
    }
}
