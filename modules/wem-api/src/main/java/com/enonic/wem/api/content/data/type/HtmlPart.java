package com.enonic.wem.api.content.data.type;


import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.Value;

public class HtmlPart
    extends ValueType
{
    HtmlPart( int key )
    {
        super( key, JavaTypeConverters.STRING_CONVERTER );
    }

    @Override
    public Value newValue( final Object value )
    {
        return new Value.HtmlPart( JavaTypeConverters.STRING_CONVERTER.convertFrom( value ) );
    }

    @Override
    public Property newProperty( final String name, final Value value )
    {
        return new Property.HtmlPart( name, value );
    }
}
