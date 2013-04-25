package com.enonic.wem.api.content.data.type;


import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.Value;

public class Text
    extends BasePropertyType
{
    Text( int key )
    {
        super( key, JavaType.STRING );
    }

    public Value toString( final Value value )
    {
        if ( isValueOfExpectedJavaClass( value ) )
        {
            return value;
        }
        else
        {
            throw new InconvertibleValueException( value, this );
        }
    }

    @Override
    public Value newValue( final Object value )
    {
        return new Value.Text( JavaType.STRING.convertFrom( value ) );
    }

    @Override
    public Value.Text.ValueBuilder newValueBuilder()
    {
        return new Value.Text.ValueBuilder();
    }

    @Override
    public Property newProperty( final String name, final Value value )
    {
        return new Property.Text( name, value );
    }
}
