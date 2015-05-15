package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

public class ReorderChildrenJson
{
    private Boolean silent;

    private Boolean manualOrder;

    private String contentId;

    private ChildOrderJson childOrder;

    private List<ReorderChildJson> orderChildren = Lists.newLinkedList();

    @JsonCreator
    public ReorderChildrenJson( @JsonProperty("silent") final Boolean silent, @JsonProperty("manualOrder") final Boolean manualOrder,
                                @JsonProperty("contentId") final String contentId,
                                @JsonProperty("childOrder") final ChildOrderJson childOrder,
                                @JsonProperty("reorderChildren") final List<ReorderChildJson> orderChildren )
    {
        this.silent = silent;
        this.manualOrder = manualOrder;
        this.contentId = contentId;
        this.childOrder= childOrder;
        this.orderChildren = orderChildren;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Boolean isSilent()
    {
        return silent;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Boolean isManualOrder()
    {
        return manualOrder;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getContentId()
    {
        return contentId;
    }

    @SuppressWarnings("UnusedDeclaration")
    public ChildOrderJson getChildOrder()
    {
        return childOrder;
    }

    @SuppressWarnings("UnusedDeclaration")
    public List<ReorderChildJson> getReorderChildren()
    {
        return orderChildren;
    }
}
