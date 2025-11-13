package com.enonic.xp.core.impl.schema.mapper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class OccurrencesMapper
{
    @JsonCreator
    public OccurrencesMapper( @JsonProperty("min") int minimum, @JsonProperty("max") int maximum )
    {
    }
}
