package com.enonic.wem.admin.rest.resource.content.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GetContentVersionsJson
{
    private Integer from;

    private Integer size;

    private final String contentId;

    @JsonCreator
    public GetContentVersionsJson( @JsonProperty("from") final Integer from, //
                                   @JsonProperty("size") final Integer size, //
                                   @JsonProperty("contentId") final String contentId )
    {
        this.from = from;
        this.size = size;
        this.contentId = contentId;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Integer getFrom()
    {
        return from;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Integer getSize()
    {
        return size;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getContentId()
    {
        return contentId;
    }
}
