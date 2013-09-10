package com.enonic.wem.admin.json.schema.content.form.inputtype;

import com.enonic.wem.api.schema.content.form.inputtype.InputType;

@SuppressWarnings("UnusedDeclaration")
public class InputTypeJson
{
    private final InputType inputType;

    private Object configJson;

    public InputTypeJson( final InputType inputType )
    {
        this.inputType = inputType;
    }

    public String getName()
    {
        return inputType.getName();
    }

    public void setConfig( final Object configJson )
    {
        this.configJson = configJson;
    }

    public Object getConfig()
    {
        return configJson;
    }
}
