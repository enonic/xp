package com.enonic.wem.admin.rest.resource.relationship.model;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RelationshipCreateParams
{
    private String type;

    private String fromContent;

    private String toContent;

    private Map<String, String> properties;

    public String getType()
    {
        return type;
    }

    public void setType( final String type )
    {
        this.type = type;
    }

    public String getFromContent()
    {
        return fromContent;
    }

    public void setFromContent( final String fromContent )
    {
        this.fromContent = fromContent;
    }

    public String getToContent()
    {
        return toContent;
    }

    public void setToContent( final String toContent )
    {
        this.toContent = toContent;
    }

    public Map<String, String> getProperties()
    {
        return properties;
    }

    public void setProperties( final Map<String, String> properties )
    {
        this.properties = properties;
    }
}
