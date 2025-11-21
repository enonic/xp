package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.inputtype.InputTypeName;

public class InstantYml
    extends InputYml
{
    public static final InputTypeName INPUT_TYPE_NAME = InputTypeName.INSTANT;

    public InstantYml()
    {
        super( INPUT_TYPE_NAME );
    }
}
