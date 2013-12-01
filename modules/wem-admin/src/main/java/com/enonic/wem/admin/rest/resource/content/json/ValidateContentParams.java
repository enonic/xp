package com.enonic.wem.admin.rest.resource.content.json;


import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.JsonNode;

@XmlRootElement
public class ValidateContentParams
{
    private String contentTypeName;

    private JsonNode contentData;

    public String getContentTypeName()
    {
        return contentTypeName;
    }

    public void setContentTypeName( final String contentTypeName )
    {
        this.contentTypeName = contentTypeName;
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
