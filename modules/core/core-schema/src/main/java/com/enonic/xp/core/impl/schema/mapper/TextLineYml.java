package com.enonic.xp.core.impl.schema.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TextLineYml
    extends InputYml
{
    public Integer maxLength;

    public String regexp;

    @JsonProperty("default")
    public String defaultValue;
}
