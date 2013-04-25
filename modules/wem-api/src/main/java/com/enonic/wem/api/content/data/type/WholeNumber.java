package com.enonic.wem.api.content.data.type;


import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.Value;

public class WholeNumber
    extends BaseValueType
{
    WholeNumber( int key )
    {
        super( key, JavaType.LONG );
    }

    @Override
    public Value newValue( final Object value )
    {
        return new Value.WholeNumber( JavaType.LONG.convertFrom( value ) );
    }

    @Override
    public Value.AbstractValueBuilder<Value.WholeNumber, Long> newValueBuilder()
    {
        return new Value.WholeNumber.ValueBuilder();
    }

    @Override
    public Property newProperty( final String name, final Value value )
    {
        return new Property.WholeNumber( name, value );
    }
}
