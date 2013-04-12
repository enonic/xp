package com.enonic.wem.api.content.data.type;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.Value;

public class DecimalNumber
    extends BaseDataType
{
    DecimalNumber( int key )
    {
        super( key, JavaType.DOUBLE );
    }

    @Override
    public Value newValue( final Object value )
    {
        return new Value.DecimalNumber( JavaType.DOUBLE.convertFrom( value ) );
    }

    @Override
    public Value.AbstractValueBuilder<Value.DecimalNumber, Double> newValueBuilder()
    {
        return new Value.DecimalNumber.ValueBuilder();
    }

    @Override
    public Data newData( final String name, final Value value )
    {
        return new Data.DecimalNumber( name, value );
    }
}
