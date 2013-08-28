package com.enonic.wem.api.data.type;


import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;

public class DateTime
    extends ValueType<org.joda.time.DateTime>
{
    DateTime( int key )
    {
        super( key, JavaTypeConverter.DateTime.GET );
    }

    @Override
    public Value newValue( final Object value )
    {
        return new Value.DateMidnight( convert( value ) );
    }

    @Override
    public Property newProperty( final String name, final Value value )
    {
        return new Property.Date( name, value );
    }
}
