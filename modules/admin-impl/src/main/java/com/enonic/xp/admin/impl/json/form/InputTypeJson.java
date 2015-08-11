package com.enonic.xp.admin.impl.json.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.annotations.Beta;

import com.enonic.xp.form.inputtype.InputType;

@Beta
@SuppressWarnings("UnusedDeclaration")
public class InputTypeJson
{
    private final InputType inputType;

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
