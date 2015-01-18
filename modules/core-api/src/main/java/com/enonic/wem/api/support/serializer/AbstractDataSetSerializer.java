package com.enonic.wem.api.support.serializer;

import com.enonic.wem.api.data.PropertySet;

public abstract class AbstractDataSetSerializer<TO_DATA_INPUT, FROM_DATA_OUTPUT>
{
    public abstract void toData( final TO_DATA_INPUT in, final PropertySet parent );

    public abstract FROM_DATA_OUTPUT fromData( final PropertySet data );

}
