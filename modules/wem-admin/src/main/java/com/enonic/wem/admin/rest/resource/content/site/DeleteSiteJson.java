package com.enonic.wem.admin.rest.resource.content.site;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.content.ContentId;

public class DeleteSiteJson
{
    private final ContentId contentId;

    @SuppressWarnings("UnusedDeclaration")
    @JsonCreator
    DeleteSiteJson( @JsonProperty("contentId") String contentId )
    {
        this.contentId = ContentId.from( contentId );
    }

    @JsonIgnore
    ContentId getDeleteSite()
    {
        return this.contentId;
    }

}
