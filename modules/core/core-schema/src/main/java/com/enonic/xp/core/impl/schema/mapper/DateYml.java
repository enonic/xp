package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.inputtype.InputTypeName;

public class DateYml
    extends InputYml
{
    public static final InputTypeName INPUT_TYPE_NAME = InputTypeName.DATE;

    public DateYml()
    {
        super( INPUT_TYPE_NAME );
    }
}
