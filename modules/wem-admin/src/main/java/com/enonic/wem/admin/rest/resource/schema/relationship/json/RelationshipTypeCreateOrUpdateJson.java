package com.enonic.wem.admin.rest.resource.schema.relationship.json;


public class RelationshipTypeCreateOrUpdateJson
{

    private String config;

    private String iconReference;

    public String getConfig()
    {
        return config;
    }

    public void setConfig( final String config )
    {
        this.config = config;
    }

    public String getIconReference()
    {
        return iconReference;
    }

    public void setIconReference( final String iconReference )
    {
        this.iconReference = iconReference;
    }
}
