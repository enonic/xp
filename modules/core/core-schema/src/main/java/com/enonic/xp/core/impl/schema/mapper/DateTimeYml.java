package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.inputtype.InputTypeName;

public class DateTimeYml
    extends InputYml
{
    public static final InputTypeName INPUT_TYPE_NAME = InputTypeName.DATE_TIME;

    public DateTimeYml()
    {
        super( INPUT_TYPE_NAME );
    }
}

