package com.enonic.xp.admin.impl.rest.resource.content.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.content.ContentId;

public class ContentSelectorQueryJson
{
    private final String expand;

    private final String queryExprString;

    private final Integer from;

    private final Integer size;

    private final ContentId contentId;

    private final String inputName;

    @JsonCreator
    public ContentSelectorQueryJson( @JsonProperty("queryExpr") final String queryExprString, //
                                     @JsonProperty("from") final Integer from, //
                                     @JsonProperty("size") final Integer size, //
                                     @JsonProperty("expand") final String expand, @JsonProperty("contentId") final String contentId,
                                     @JsonProperty("inputName") final String inputName )
    {

        this.from = from;
        this.size = size;
        this.queryExprString = queryExprString;
        this.contentId = ContentId.from( contentId );
        this.expand = expand != null ? expand : "none";
        this.inputName = inputName;
    }

    @JsonIgnore
    public String getQueryExprString()
    {
        return queryExprString;
    }

    @JsonIgnore
    public Integer getFrom()
    {
        return from;
    }

    @JsonIgnore
    public Integer getSize()
    {
        return size;
    }

    @JsonIgnore
    public ContentId getContentId()
    {
        return contentId;
    }

    @JsonIgnore
    public String getExpand()
    {
        return expand;
    }

    @JsonIgnore
    public String getInputName()
    {
        return inputName;
    }
}
