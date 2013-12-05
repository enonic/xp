package com.enonic.wem.admin.rest.resource.content.site;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.command.content.site.DeleteSite;
import com.enonic.wem.api.content.ContentId;

public class DeleteSiteJson
{
    private final DeleteSite deleteSite;

    @SuppressWarnings("UnusedDeclaration")
    @JsonCreator
    DeleteSiteJson( @JsonProperty("contentId") String contentId )
    {
        this.deleteSite = new DeleteSite().content( ContentId.from( contentId ) );
    }

    @JsonIgnore
    DeleteSite getDeleteSite()
    {
        return this.deleteSite;
    }

}
