package com.enonic.wem.admin.json.schema.relationship;

import com.enonic.wem.admin.json.ItemJson;

public abstract class AbstractRelationshipTypeJson
    extends ItemJson
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
