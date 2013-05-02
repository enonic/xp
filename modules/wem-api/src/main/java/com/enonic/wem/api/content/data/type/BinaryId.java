package com.enonic.wem.api.content.data.type;


import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.Value;

public class BinaryId
    extends ValueType
{
    BinaryId( int key )
    {
        super( key, JavaTypeConverter.BinaryId.GET );
    }

    @Override
    public Value newValue( final Object value )
    {
        return new Value.BinaryId( JavaTypeConverter.BinaryId.GET.convertFrom( value ) );
    }

    @Override
    public Property newProperty( final String name, final Value value )
    {
        return new Property.BinaryId( name, value );
    }
}
