package com.enonic.wem.admin.rest.resource.content.site;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.site.GetNearestSiteByContentId;
import com.enonic.wem.api.content.ContentId;

public class GetNearestSiteJson
{
    private final GetNearestSiteByContentId command;

    @SuppressWarnings("UnusedDeclaration")
    @JsonCreator
    GetNearestSiteJson( @JsonProperty("contentId") String contentId )
    {
        this.command = Commands.site().getNearestSite().content( ContentId.from( contentId ) );
    }

    @JsonIgnore
    GetNearestSiteByContentId getGetNearestSiteByContentId()
    {
        return this.command;
    }
}
