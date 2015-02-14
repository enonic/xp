package com.enonic.xp.admin.impl.rest.resource.relationship.json;

import java.time.Instant;
import java.util.Map;

import com.enonic.xp.admin.impl.json.ItemJson;
import com.enonic.wem.api.relationship.Relationship;
import com.enonic.wem.api.relationship.RelationshipId;
import com.enonic.wem.api.security.PrincipalKey;

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

    public Instant getCreatedTime()
    {
        return model.getCreatedTime();
    }

    public Instant getModifiedTime()
    {
        return model.getModifiedTime();
    }

    public PrincipalKey getCreator()
    {
        return model.getCreator();
    }

    public PrincipalKey getModifier()
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
