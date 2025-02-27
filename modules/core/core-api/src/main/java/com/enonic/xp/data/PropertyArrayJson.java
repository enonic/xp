package com.enonic.xp.data;

import java.util.List;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class PropertyArrayJson
{
    public String name;

    public String type;

    public List<PropertyValueJson> values;
}
