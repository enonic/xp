package com.enonic.wem.admin.rest.resource.content.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SetChildOrderJson
{
    private ChildOrderJson childOrder;

    private String contentId;

    @JsonCreator
    public SetChildOrderJson( @JsonProperty("contentId") final String contentId, //
                              @JsonProperty("childOrder") final ChildOrderJson childOrder )
    {
        this.childOrder = childOrder;
        this.contentId = contentId;
    }

    @SuppressWarnings("UnusedDeclaration")
    public ChildOrderJson getChildOrder()
    {
        return childOrder;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getContentId()
    {
        return contentId;
    }


}
