package com.enonic.xp.lib.schema.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class OccurrencesMixin
{
    @JsonCreator
    public OccurrencesMixin( @JsonProperty("minimum") int minimum, @JsonProperty("maximum") int maximum )
    {
    }
}
