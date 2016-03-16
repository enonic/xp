package com.enonic.xp.admin.impl.rest.resource.content.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentVersionId;

public class SetActiveVersionJson
{
    private final ContentId contentId;

    private final ContentVersionId contentVersionId;


    public SetActiveVersionJson( @JsonProperty("contentId") final ContentId contentId, //
                                 @JsonProperty("contentVersionId") final ContentVersionId contentVersionId )
    {
        this.contentId = contentId;
        this.contentVersionId = contentVersionId;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public ContentVersionId getContentVersionId()
    {
        return contentVersionId;
    }
}
