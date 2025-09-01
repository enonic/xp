package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.inputtype.InputTypeName;

public class GeoPointYml
    extends InputYml
{

    public static final InputTypeName INPUT_TYPE_NAME = InputTypeName.GEO_POINT;

    public GeoPointYml()
    {
        super( INPUT_TYPE_NAME );
    }
}
