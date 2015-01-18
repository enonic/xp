package com.enonic.wem.api.support.serializer;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.PropertySet;

public abstract class AbstractDataListSerializer<TO_DATA_INPUT, FROM_DATA_OUTPUT>
{
    public abstract void toData( final TO_DATA_INPUT in, PropertySet parent );

    public abstract FROM_DATA_OUTPUT fromData( final Iterable<Property> dataList );

}
