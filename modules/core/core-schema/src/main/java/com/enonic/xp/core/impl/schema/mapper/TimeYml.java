package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.inputtype.InputTypeName;

public class TimeYml
    extends InputYml
{
    public static final InputTypeName INPUT_TYPE_NAME = InputTypeName.TIME;

    public TimeYml()
    {
        super( INPUT_TYPE_NAME );
    }
}
