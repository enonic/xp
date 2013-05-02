package com.enonic.wem.api.content.data.type;


import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.Value;

public class DateMidnight
    extends ValueType
{
    DateMidnight( int key )
    {
        super( key, JavaTypeConverters.DATE_MIDNIGHT_CONVERTER );
    }

    @Override
    public Value newValue( final Object value )
    {
        return new Value.Date( JavaTypeConverters.DATE_MIDNIGHT_CONVERTER.convertFrom( value ) );
    }

    @Override
    public Property newProperty( final String name, final Value value )
    {
        return new Property.Date( name, value );
    }
}
