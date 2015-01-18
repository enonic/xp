package com.enonic.wem.admin.rest.resource.content.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReorderChildJson
{
    private final String contentId;

    private final String moveBefore;

    @JsonCreator
    public ReorderChildJson( @JsonProperty("contentId") final String contentId, //
                             @JsonProperty("moveBefore") final String moveBefore )
    {
        this.contentId = contentId;
        this.moveBefore = moveBefore;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getContentId()
    {
        return contentId;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getMoveBefore()
    {
        return moveBefore;
    }
}
