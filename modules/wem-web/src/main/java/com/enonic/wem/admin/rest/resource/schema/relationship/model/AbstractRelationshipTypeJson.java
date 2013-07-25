package com.enonic.wem.admin.rest.resource.schema.relationship.model;

import com.enonic.wem.admin.rest.resource.model.Item;

public abstract class AbstractRelationshipTypeJson
    extends Item
{
    protected AbstractRelationshipTypeJson()
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
