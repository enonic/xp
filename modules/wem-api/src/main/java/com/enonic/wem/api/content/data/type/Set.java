package com.enonic.wem.api.content.data.type;


import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.Value;

public class Set
    extends BaseDataType
{
    Set( int key )
    {
        super( key, JavaType.DATA_SET );
    }

    @Override
    public Value newValue( final Object value )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Value.AbstractValueBuilder newValueBuilder()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Data newData( final String name, final Value value )
    {
        throw new UnsupportedOperationException();
    }
}
