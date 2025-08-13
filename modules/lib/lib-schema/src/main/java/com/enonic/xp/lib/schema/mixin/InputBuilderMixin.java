package com.enonic.xp.lib.schema.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeDefault;

@JsonPOJOBuilder(withPrefix = "")
public abstract class InputBuilderMixin
{
    @JsonCreator
    public static Input.Builder create()
    {
        return Input.create();
    }

    @JsonProperty("defaultValue")
    abstract Input.Builder defaultValue( InputTypeDefault value );
}
