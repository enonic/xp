package com.enonic.xp.core.impl.schema.mapper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import com.enonic.xp.form.InlineMixin;

@JsonDeserialize(builder = InlineMixin.Builder.class)
public abstract class InlineMixinMapper
{
    @JsonIgnoreProperties({"type"})
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder
    {
        @JsonCreator
        public static InlineMixin.Builder create()
        {
            return InlineMixin.create();
        }

        @JsonProperty("mixin")
        abstract InlineMixin.Builder mixin( String mixin );
    }
}
