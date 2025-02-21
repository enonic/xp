package com.enonic.xp.json;

import com.fasterxml.jackson.databind.ObjectMapper;

@Deprecated
public final class ObjectMapperHelper
{
    private ObjectMapperHelper()
    {
    }

    public static ObjectMapper create()
    {
        return com.enonic.xp.core.internal.json.ObjectMapperHelper.create();
    }
}
