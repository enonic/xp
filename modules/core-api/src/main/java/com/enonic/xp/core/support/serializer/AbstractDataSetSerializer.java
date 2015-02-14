package com.enonic.xp.core.support.serializer;

import com.enonic.xp.core.data.PropertySet;

public abstract class AbstractDataSetSerializer<TO_DATA_INPUT, FROM_DATA_OUTPUT>
{
    public abstract void toData( final TO_DATA_INPUT in, final PropertySet parent );

    public abstract FROM_DATA_OUTPUT fromData( final PropertySet data );

}
