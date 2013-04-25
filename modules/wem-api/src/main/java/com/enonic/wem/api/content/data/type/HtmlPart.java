package com.enonic.wem.api.content.data.type;


import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.Value;

public class HtmlPart
    extends BaseValueType
    implements ValueType
{
    HtmlPart( int key )
    {
        super( key, JavaType.STRING );
    }

    @Override
    public Value newValue( final Object value )
    {
        return new Value.HtmlPart( JavaType.STRING.convertFrom( value ) );
    }

    @Override
    public Value.HtmlPart.ValueBuilder newValueBuilder()
    {
        return new Value.HtmlPart.ValueBuilder();
    }

    @Override
    public Property newProperty( final String name, final Value value )
    {
        return new Property.HtmlPart( name, value );
    }
}
