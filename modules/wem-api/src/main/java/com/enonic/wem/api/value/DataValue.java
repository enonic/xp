package com.enonic.wem.api.value;

import com.enonic.wem.api.data.RootDataSet;

public final class DataValue
    extends Value<RootDataSet>
{
    public DataValue( final RootDataSet object )
    {
        super( ValueType.DATA, object );
    }

    @Override
    public String asString()
    {
        return this.object.toString();
    }
}
