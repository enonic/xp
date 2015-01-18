package com.enonic.wem.admin.rest.resource.content.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

public class ReorderChildrenJson
{
    private List<ReorderChildJson> orderChildren = Lists.newLinkedList();

    @JsonCreator
    public ReorderChildrenJson( @JsonProperty("reorderChildren") final List<ReorderChildJson> orderChildren )
    {
        this.orderChildren = orderChildren;
    }


    @SuppressWarnings("UnusedDeclaration")
    public List<ReorderChildJson> getReorderChildren()
    {
        return orderChildren;
    }
}
