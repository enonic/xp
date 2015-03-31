package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

public class ReorderChildrenJson
{
    private Boolean silent;

    private String contentId;

    private List<ReorderChildJson> orderChildren = Lists.newLinkedList();

    @JsonCreator
    public ReorderChildrenJson( @JsonProperty("silent") final Boolean silent, @JsonProperty("contentId") final String contentId,
                                @JsonProperty("reorderChildren") final List<ReorderChildJson> orderChildren )
    {
        this.silent = silent;
        this.contentId = contentId;
        this.orderChildren = orderChildren;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Boolean isSilent()
    {
        return silent;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getContentId()
    {
        return contentId;
    }

    @SuppressWarnings("UnusedDeclaration")
    public List<ReorderChildJson> getReorderChildren()
    {
        return orderChildren;
    }
}
