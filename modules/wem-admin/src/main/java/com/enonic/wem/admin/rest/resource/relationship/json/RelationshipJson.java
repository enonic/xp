package com.enonic.wem.admin.rest.resource.relationship.json;

import java.util.Map;

import org.joda.time.DateTime;

import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.relationship.Relationship;
import com.enonic.wem.api.relationship.RelationshipId;

public class RelationshipJson
    implements ItemJson
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

    public DateTime getCreatedTime()
    {
        return model.getCreatedTime();
    }

    public DateTime getModifiedTime()
    {
        return model.getModifiedTime();
    }

    public UserKey getCreator()
    {
        return model.getCreator();
    }

    public UserKey getModifier()
    {
        return model.getModifier();
    }

    public RelationshipId getId()
    {
        return this.model.getId();
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
