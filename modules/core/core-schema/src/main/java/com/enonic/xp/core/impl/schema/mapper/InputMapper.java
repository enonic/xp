package com.enonic.xp.core.impl.schema.mapper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeDefault;
import com.enonic.xp.inputtype.InputTypeProperty;

@JsonDeserialize(builder = Input.Builder.class)
public abstract class InputMapper
{
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder
    {
        @JsonCreator
        public static Input.Builder create()
        {
            return Input.create();
        }

        @JsonProperty("type")
        abstract Input.Builder inputType( String type );

        @JsonProperty("defaultValue")
        abstract Input.Builder defaultValue( InputTypeDefault value );

        @JsonProperty("config")
        abstract Input.Builder inputTypeProperties( Iterable<InputTypeProperty> properties );
    }
}
