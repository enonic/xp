package com.enonic.xp.lib.schema.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import com.enonic.xp.inputtype.InputTypeDefault;
import com.enonic.xp.inputtype.InputTypeProperty;

@JsonPOJOBuilder(withPrefix = "")
public abstract class InputTypeDefaultBuilderMixin
{
    @JsonCreator
    public static InputTypeDefault.Builder create()
    {
        return InputTypeDefault.create();
    }

    @JsonProperty("property")
    public InputTypeDefault.Builder propertyFromYaml( InputTypeProperty prop )
    {
        return property( prop );
    }

    abstract InputTypeDefault.Builder property( InputTypeProperty property );
}
