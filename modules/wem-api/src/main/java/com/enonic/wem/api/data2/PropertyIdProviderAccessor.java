package com.enonic.wem.api.data2;


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
