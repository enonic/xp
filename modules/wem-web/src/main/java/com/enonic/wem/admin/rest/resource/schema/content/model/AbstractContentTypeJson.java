package com.enonic.wem.admin.rest.resource.schema.content.model;

import com.enonic.wem.admin.rest.resource.model.Item;

public abstract class AbstractContentTypeJson
    extends Item
{
    protected AbstractContentTypeJson()
    {
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