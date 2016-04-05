package com.enonic.xp.admin.impl.rest.resource.content.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentVersionId;

public class SetActiveVersionJson
{
    private final ContentId contentId;

    private final ContentVersionId versionId;


    public SetActiveVersionJson( @JsonProperty("contentId") final ContentId contentId, //
                                 @JsonProperty("versionId") final ContentVersionId versionId )
    {
        this.contentId = contentId;
        this.versionId = versionId;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public ContentVersionId getVersionId()
    {
        return versionId;
    }
}
