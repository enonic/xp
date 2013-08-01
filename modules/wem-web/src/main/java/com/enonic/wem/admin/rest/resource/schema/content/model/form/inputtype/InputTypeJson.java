package com.enonic.wem.admin.rest.resource.schema.content.model.form.inputtype;

import com.enonic.wem.admin.rest.resource.model.Item;
import com.enonic.wem.api.schema.content.form.inputtype.BaseInputType;
import com.enonic.wem.api.schema.content.form.inputtype.config.AbstractInputTypeConfigJson;

public class InputTypeJson
    extends Item
{
    private final BaseInputType baseInputType;

    private AbstractInputTypeConfigJson configJson;

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

    public void setConfig( final AbstractInputTypeConfigJson configJson )
    {
        this.configJson = configJson;
    }

    public AbstractInputTypeConfigJson getConfig()
    {
        return configJson;
    }

    @Override
    public boolean getEditable()
    {
        return false;
    }

    @Override
    public boolean getDeletable()
    {
        return false;
    }
}
