package com.enonic.xp.core.impl.content.serializer;

import com.enonic.xp.data.PropertySet;

public abstract class AbstractDataSetSerializer<DATA>
{
    public abstract void toData( DATA in, PropertySet parent );

    public abstract DATA fromData( PropertySet data );
}
