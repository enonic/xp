package com.enonic.wem.api.content.data.type;


import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.Value;

public class WholeNumber
    extends ValueType
{
    WholeNumber( int key )
    {
        super( key, JavaTypeConverter.Long.GET );
    }

    @Override
    public Value newValue( final Object value )
    {
        return new Value.WholeNumber( JavaTypeConverter.Long.GET.convertFrom( value ) );
    }

    @Override
    public Property newProperty( final String name, final Value value )
    {
        return new Property.WholeNumber( name, value );
    }
}
