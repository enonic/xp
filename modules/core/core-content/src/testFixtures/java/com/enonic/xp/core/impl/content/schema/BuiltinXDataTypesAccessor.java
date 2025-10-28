package com.enonic.xp.core.impl.content.schema;

import com.enonic.xp.schema.xdata.MixinDescriptors;

public class BuiltinXDataTypesAccessor
{
    private static final BuiltinMixinTypes BUILTIN_XDATA_TYPES = new BuiltinMixinTypes();

    private BuiltinXDataTypesAccessor()
    {
    }

    public static MixinDescriptors getAll()
    {
        return BUILTIN_XDATA_TYPES.getAll();
    }
}
