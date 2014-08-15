package com.enonic.wem.admin.rest.resource.schema.relationship;


import com.enonic.wem.admin.json.icon.ThumbnailJson;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;

public class RelationshipTypeUpdateJson
{
    private RelationshipTypeName relationshipTypeToUpdate;

    private RelationshipTypeName name;

    private String config;

    private ThumbnailJson icon;

    public RelationshipTypeName getRelationshipTypeToUpdate()
    {
        return relationshipTypeToUpdate;
    }

    public void setRelationshipTypeToUpdate( final RelationshipTypeName relationshipTypeToUpdate )
    {
        this.relationshipTypeToUpdate = relationshipTypeToUpdate;
    }

    public RelationshipTypeName getName()
    {
        return name;
    }

    public void setName( final String name )
    {
        this.name = RelationshipTypeName.from( name );
    }

    public String getConfig()
    {
        return config;
    }

    public void setConfig( final String config )
    {
        this.config = config;
    }

    public ThumbnailJson getThumbnailJson()
    {
        return icon;
    }

    public void setIcon( final ThumbnailJson icon )
    {
        this.icon = icon;
    }
}
