package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.inputtype.InputTypeName;

public class ContentTypeFilterYml
    extends InputYml
{
    public static final InputTypeName INPUT_TYPE_NAME = InputTypeName.CONTENT_TYPE_FILTER;

    public ContentTypeFilterYml()
    {
        super( INPUT_TYPE_NAME );
    }
}
