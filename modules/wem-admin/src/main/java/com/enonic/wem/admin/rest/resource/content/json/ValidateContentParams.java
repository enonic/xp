package com.enonic.wem.admin.rest.resource.content.json;


import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.data.DataJson;

@XmlRootElement
public class ValidateContentParams
{
    private String contentTypeName;

    private List<DataJson> contentDataAsDataJsonList;

    public String getContentTypeName()
    {
        return contentTypeName;
    }

    public void setContentTypeName( final String contentTypeName )
    {
        this.contentTypeName = contentTypeName;
    }

    public List<DataJson> getContentData()
    {
        return contentDataAsDataJsonList;
    }

    public void setContentData( final List<DataJson> contentData )
    {
        this.contentDataAsDataJsonList = contentData;
    }
}
