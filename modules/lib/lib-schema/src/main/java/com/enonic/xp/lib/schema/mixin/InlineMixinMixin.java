package com.enonic.xp.lib.schema.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import com.enonic.xp.form.InlineMixin;

@JsonDeserialize(builder = InlineMixin.Builder.class)
public abstract class InlineMixinMixin
{
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
