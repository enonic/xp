package com.enonic.wem.api.content.data.type;


import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.Value;

public class Text
    extends BaseDataType
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
    public Data newData( final String name, final Value value )
    {
        return new Data.Text( name, value );
    }
}
