package com.enonic.wem.api.data.type;


import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;

public class HtmlPart
    extends ValueType<java.lang.String>
{
    HtmlPart( int key )
    {
        super( key, JavaTypeConverters.STRING );
    }

    @Override
    public Value newValue( final Object value )
    {
        return new Value.HtmlPart( convert( value ) );
    }

    @Override
    public Property newProperty( final java.lang.String name, final Value value )
    {
        return new Property.HtmlPart( name, value );
    }
}
