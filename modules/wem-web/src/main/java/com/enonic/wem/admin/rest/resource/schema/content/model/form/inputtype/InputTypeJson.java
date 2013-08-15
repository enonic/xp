package com.enonic.wem.admin.rest.resource.schema.content.model.form.inputtype;

import com.enonic.wem.api.schema.content.form.inputtype.BaseInputType;

public class InputTypeJson
{
    private final BaseInputType baseInputType;

    private Object configJson;

    public InputTypeJson( final BaseInputType baseInputType )
    {
        this.baseInputType = baseInputType;
    }

    public String getName()
    {
        return baseInputType.getName();
    }

    public boolean isBuiltIn()
    {
        return baseInputType.isBuiltIn();
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
