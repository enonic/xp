package com.enonic.xp.data;

import java.util.UUID;

public final class UUIDPropertyIdProvider
    implements PropertyIdProvider
{
    @Override
    public PropertyId nextId()
    {
        return new PropertyId( UUID.randomUUID().toString() );
    }
}
