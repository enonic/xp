package com.enonic.wem.api.content.data.type;

import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.Value;

public class ContentId
    extends BaseValueType
{
    ContentId( int key )
    {
        super( key, JavaType.CONTENT_ID );
    }

    @Override
    public Value newValue( final Object value )
    {
        return new Value.ContentId( JavaType.CONTENT_ID.convertFrom( value ) );
    }

    @Override
    public Value.AbstractValueBuilder<Value.ContentId, com.enonic.wem.api.content.ContentId> newValueBuilder()
    {
        return new Value.ContentId.ValueBuilder();
    }

    @Override
    public Property newProperty( final String name, final Value value )
    {
        return new Property.ContentId( name, value );
    }
}
