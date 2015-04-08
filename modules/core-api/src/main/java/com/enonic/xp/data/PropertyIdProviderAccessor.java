package com.enonic.xp.data;

import com.google.common.annotations.Beta;

@Beta
public class PropertyIdProviderAccessor
    extends ThreadLocal<PropertyIdProvider>
{
    private static final PropertyIdProviderAccessor INSTANCE = new PropertyIdProviderAccessor();

    @Override
    protected PropertyIdProvider initialValue()
    {
        return new PropertyTree.DefaultPropertyIdProvider();
    }

    public static PropertyIdProviderAccessor instance()
    {
        return INSTANCE;
    }
}
