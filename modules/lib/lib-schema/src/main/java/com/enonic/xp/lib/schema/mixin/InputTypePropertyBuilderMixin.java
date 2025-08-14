package com.enonic.xp.lib.schema.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import com.enonic.xp.inputtype.InputTypeProperty;

@JsonPOJOBuilder(withPrefix = "")
public abstract class InputTypePropertyBuilderMixin
{
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static InputTypeProperty.Builder create( @JsonProperty("name") String name, @JsonProperty("value") String value )
    {
        return InputTypeProperty.create( name, value );
    }

    @JsonProperty("attribute")
    abstract InputTypeProperty.Builder attribute( @JsonProperty("name") String name, @JsonProperty("value") String value );
}
