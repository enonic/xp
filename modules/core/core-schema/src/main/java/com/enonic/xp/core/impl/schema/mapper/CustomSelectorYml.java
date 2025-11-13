package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.inputtype.InputTypeName;

public class CustomSelectorYml
    extends InputYml
{
    public static final InputTypeName INPUT_TYPE_NAME = InputTypeName.CUSTOM_SELECTOR;

    public CustomSelectorYml()
    {
        super( INPUT_TYPE_NAME );
    }
}
