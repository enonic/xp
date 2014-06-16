package com.enonic.wem.admin.rest.resource.content.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.content.ContentId;

public class PublishContentJson
{
    private final ContentId contentId;

    @JsonCreator
    PublishContentJson( @JsonProperty("contentId") final String contentIdString )
    {
        this.contentId = ContentId.from( contentIdString );
    }

    @SuppressWarnings( "UnusedDeclaration" )
    public ContentId getContentId()
    {
        return contentId;
    }

}
