package com.enonic.xp.lib.schema.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import com.enonic.xp.form.InlineMixin;

@JsonPOJOBuilder(withPrefix = "")
public abstract class InlineMixinBuilderMixin
{
    @JsonCreator
    public static InlineMixin.Builder create()
    {
        return InlineMixin.create();
    }

    @JsonProperty("mixin")
    abstract InlineMixin.Builder mixin( String mixin );
}
