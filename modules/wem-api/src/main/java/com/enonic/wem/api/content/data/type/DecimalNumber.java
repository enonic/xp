package com.enonic.wem.api.content.data.type;

import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.Value;

public class DecimalNumber
    extends ValueType
{
    DecimalNumber( int key )
    {
        super( key, JavaTypeConverters.DOUBLE_CONVERTER );
    }

    @Override
    public Value newValue( final Object value )
    {
        return new Value.DecimalNumber( JavaTypeConverters.DOUBLE_CONVERTER.convertFrom( value ) );
    }

    @Override
    public Property newProperty( final String name, final Value value )
    {
        return new Property.DecimalNumber( name, value );
    }
}
