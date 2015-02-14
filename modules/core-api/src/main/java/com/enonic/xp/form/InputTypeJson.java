package com.enonic.xp.form;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.form.inputtype.InputType;
import com.enonic.xp.form.inputtype.InputTypeResolver;

@SuppressWarnings("UnusedDeclaration")
public class InputTypeJson
{
    private final InputType inputType;

    @JsonCreator
    public InputTypeJson( @JsonProperty("name") String name, @JsonProperty("config") String config )
    {
        this.inputType = InputTypeResolver.get().resolve( name );
    }

    public InputTypeJson( final InputType inputType )
    {
        this.inputType = inputType;
    }

    @JsonIgnore
    public InputType getInputType()
    {
        return inputType;
    }

    public String getName()
    {
        return inputType.getName();
    }
}
