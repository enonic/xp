package com.enonic.xp.core.impl.content.schema;

import com.enonic.xp.schema.mixin.MixinDescriptors;

public class BuiltinMixinsTypesAccessor
{
    private static final BuiltinMixinTypes BUILTIN_MIXINS_TYPES = new BuiltinMixinTypes();

    private BuiltinMixinsTypesAccessor()
    {
    }

    public static MixinDescriptors getAll()
    {
        return BUILTIN_MIXINS_TYPES.getAll();
    }
}
