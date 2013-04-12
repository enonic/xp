package com.enonic.wem.api.content.data.type;


import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.Value;

public class HtmlPart
    extends BaseDataType
    implements DataType
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
    public Data newData( final String name, final Value value )
    {
        return new Data.HtmlPart( name, value );
    }
}
