package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.inputtype.InputTypeName;

public class TagYml
    extends InputYml
{

    public static final InputTypeName INPUT_TYPE_NAME = InputTypeName.TAG;

    public TagYml()
    {
        super( INPUT_TYPE_NAME );
    }
}
