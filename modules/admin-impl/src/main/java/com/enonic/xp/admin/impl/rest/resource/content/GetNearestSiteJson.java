package com.enonic.xp.admin.impl.rest.resource.content;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.content.ContentId;

public class GetNearestSiteJson
{
    private final ContentId contentId;

    @SuppressWarnings("UnusedDeclaration")
    @JsonCreator
    GetNearestSiteJson( @JsonProperty("contentId") String contentId )
    {
        this.contentId = ContentId.from( contentId );
    }

    @JsonIgnore
    ContentId getGetNearestSiteByContentId()
    {
        return this.contentId;
    }
}
