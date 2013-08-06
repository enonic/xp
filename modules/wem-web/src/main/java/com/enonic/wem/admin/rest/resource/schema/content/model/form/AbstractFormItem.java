package com.enonic.wem.admin.rest.resource.schema.content.model.form;

import com.enonic.wem.admin.rest.resource.model.Item;

public abstract class AbstractFormItem
    extends Item
{
    protected AbstractFormItem()
    {
    }

    public abstract String getName();

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
