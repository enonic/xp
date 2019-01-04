package com.enonic.xp.core.impl.content.serializer;

import com.google.common.annotations.Beta;

import com.enonic.xp.data.PropertySet;

@Beta
public abstract class AbstractDataSetSerializer<DATA>
{
    public abstract void toData( final DATA in, final PropertySet parent );

    public abstract DATA fromData( final PropertySet data );

}
