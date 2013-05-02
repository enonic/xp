package com.enonic.wem.api.content.data.type;

import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.Value;

public class ContentId
    extends ValueType
{
    ContentId( int key )
    {
        super( key, JavaTypeConverter.ContentId.GET );
    }

    @Override
    public Value newValue( final Object value )
    {
        return new Value.ContentId( JavaTypeConverter.ContentId.GET.convertFrom( value ) );
    }

    @Override
    public Property newProperty( final String name, final Value value )
    {
        return new Property.ContentId( name, value );
    }
}
