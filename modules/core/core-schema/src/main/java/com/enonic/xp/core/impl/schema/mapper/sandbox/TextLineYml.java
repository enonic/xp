package com.enonic.xp.core.impl.schema.mapper.sandbox;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class TextLineYml
    extends InputYml
{
    public Integer maxLength;

    public String regexp;

    @JsonProperty("default")
    public String defaultValue;
}
