package com.enonic.wem.api.content.data.type;


import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.Value;

public class Xml
    extends BaseDataType
{
    Xml( int key )
    {
        super( key, JavaType.STRING );
    }

    @Override
    public Value newValue( final Object value )
    {
        return new Value.Xml( JavaType.STRING.convertFrom( value ) );
    }

    @Override
    public Value.AbstractValueBuilder newValueBuilder()
    {
        return new Value.Xml.ValueBuilder();
    }

    @Override
    public Data newData( final String name, final Value value )
    {
        return new Data.Xml( name, value );
    }
}
