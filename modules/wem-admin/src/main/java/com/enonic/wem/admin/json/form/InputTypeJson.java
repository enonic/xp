package com.enonic.wem.admin.json.form;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.form.inputtype.InputType;
import com.enonic.wem.core.form.inputtype.InputTypeResolver;

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
