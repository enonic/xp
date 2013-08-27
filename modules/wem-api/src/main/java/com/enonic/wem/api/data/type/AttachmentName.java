package com.enonic.wem.api.data.type;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;

public class AttachmentName
    extends ValueType<String>
{
    AttachmentName( int key )
    {
        super( key, JavaTypeConverter.String.GET );
    }

    @Override
    public Value newValue( final Object value )
    {
        return new Value.AttachmentName( convert( value ) );
    }

    @Override
    public Property newProperty( final String name, final Value value )
    {
        return new Property.AttachmentName( name, value.asString() );
    }
}
