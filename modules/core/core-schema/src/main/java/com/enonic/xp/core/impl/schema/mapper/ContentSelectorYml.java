package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.inputtype.InputTypeName;

public class ContentSelectorYml
    extends InputYml
{
    public static final InputTypeName INPUT_TYPE_NAME = InputTypeName.CONTENT_SELECTOR;

    protected ContentSelectorYml()
    {
        super( INPUT_TYPE_NAME );
    }
}
