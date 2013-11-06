package com.enonic.wem.api.data.type;


import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;

public class Data
    extends ValueType<com.enonic.wem.api.data.RootDataSet>
{
    Data( int key )
    {
        super( key, JavaTypeConverter.Data.GET );
    }

    @Override
    public Value newValue( final Object value )
    {
        return new Value.Data( convert( value ) );
    }

    @Override
    public Property newProperty( final java.lang.String name, final Value value )
    {
        return new Property.String( name, value );
    }
}
