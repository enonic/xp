package com.enonic.wem.admin.rest.resource.relationship.model;

import java.util.Map;

import com.enonic.wem.admin.rest.resource.model.Item;
import com.enonic.wem.api.relationship.Relationship;

public class RelationshipJson
    extends Item
{
    private final Relationship model;

    public RelationshipJson( final Relationship model )
    {
        this.model = model;
    }

    public String getType()
    {
        return this.model.getType().toString();
    }

    public String getFromContent()
    {
        return this.model.getFromContent().toString();
    }

    public String getToContent()
    {
        return this.model.getToContent().toString();
    }

    public String getManagingData()
    {
        return ( this.model.getManagingData() != null ) ? this.model.getManagingData().toString() : null;
    }

    public Map<String, String> getProperties()
    {
        final Map<String, String> properties = model.getProperties();

        return properties;
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
