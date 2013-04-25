package com.enonic.wem.api.content.data.type;


import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.Value;

public class Set
    extends BasePropertyType
{
    Set( int key )
    {
        super( key, JavaType.DATA_SET );
    }

    @Override
    public Value newValue( final Object value )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Value.AbstractValueBuilder newValueBuilder()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Property newProperty( final String name, final Value value )
    {
        throw new UnsupportedOperationException();
    }
}
