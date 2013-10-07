package com.enonic.wem.api.data.type;


import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;

public class DateMidnight
    extends ValueType<org.joda.time.DateMidnight>
{
    DateMidnight( int key )
    {
        super( key, JavaTypeConverter.DateMidnight.GET );
    }

    @Override
    public Value newValue( final Object value )
    {
        return new Value.DateMidnight( convert( value ) );
    }

    @Override
    public Property newProperty( final java.lang.String name, final Value value )
    {
        return new Property.Date( name, value );
    }
}
