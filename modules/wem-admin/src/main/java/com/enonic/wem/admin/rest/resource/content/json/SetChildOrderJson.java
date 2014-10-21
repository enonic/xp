package com.enonic.wem.admin.rest.resource.content.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SetChildOrderJson
{
    private String childOrder;

    private String contentId;

    @JsonCreator
    public SetChildOrderJson( @JsonProperty("contentId") final String contentId, //
                              @JsonProperty("childOrder") final String childOrder )
    {
        this.childOrder = childOrder;
        this.contentId = contentId;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getChildOrder()
    {
        return childOrder;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getContentId()
    {
        return contentId;
    }
}
