package com.enonic.xp.data;

import com.google.common.annotations.Beta;

@Beta
public final class PropertyIdProviderAccessor
    extends ThreadLocal<PropertyIdProvider>
{
    private static final PropertyIdProviderAccessor INSTANCE = new PropertyIdProviderAccessor();

    @Override
    protected PropertyIdProvider initialValue()
    {
        return new UUIDPropertyIdProvider();
    }

    public static PropertyIdProviderAccessor instance()
    {
        return INSTANCE;
    }
}
