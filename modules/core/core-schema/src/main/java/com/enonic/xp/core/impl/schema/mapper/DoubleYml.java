package com.enonic.xp.core.impl.schema.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DoubleYml
    extends InputYml
{
    public Double min;

    public Double max;

    @JsonProperty("default")
    public Double defaultValue;
}
