package com.enonic.wem.admin.rest.resource.content.json;


import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.JsonNode;

@XmlRootElement
public class ValidateContentParams
{
    private String qualifiedContentTypeName;

    private JsonNode contentData;

    public String getQualifiedContentTypeName()
    {
        return qualifiedContentTypeName;
    }

    public void setQualifiedContentTypeName( final String qualifiedContentTypeName )
    {
        this.qualifiedContentTypeName = qualifiedContentTypeName;
    }

    public JsonNode getContentData()
    {
        return contentData;
    }

    public void setContentData( final JsonNode contentData )
    {
        this.contentData = contentData;
    }
}
