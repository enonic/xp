package com.enonic.xp.admin.impl.rest.resource.content.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SetChildOrderJson
{
    private Boolean silent;

    private String contentId;

    private ChildOrderJson childOrder;

    @JsonCreator
    public SetChildOrderJson( @JsonProperty("silent") final Boolean silent, @JsonProperty("contentId") final String contentId,
                              @JsonProperty("childOrder") final ChildOrderJson childOrder )
    {
        this.silent = silent;
        this.contentId = contentId;
        this.childOrder = childOrder;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Boolean isSilent()
    {
        return silent;
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
