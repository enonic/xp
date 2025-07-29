package com.enonic.xp.core.impl.schema.xdata;

import com.enonic.xp.schema.xdata.XDatas;

public class BuiltinXDataTypesAccessor
{
    private static final BuiltinXDataTypes BUILTIN_XDATA_TYPES = new BuiltinXDataTypes();

    private BuiltinXDataTypesAccessor()
    {
    }

    public static XDatas getAll()
    {
        return BUILTIN_XDATA_TYPES.getAll();
    }
}
