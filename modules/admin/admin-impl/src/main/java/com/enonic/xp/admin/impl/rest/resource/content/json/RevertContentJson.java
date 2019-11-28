package com.enonic.xp.admin.impl.rest.resource.content.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RevertContentJson
{

    private final String contentKey;

    private final String versionId;

    @JsonCreator
    public RevertContentJson( final @JsonProperty(value = "contentKey", required = true) String contentKey,
                              final @JsonProperty(value = "versionId", required = true) String versionId )
    {
        this.contentKey = contentKey;
        this.versionId = versionId;
    }

    public String getContentKey()
    {
        return contentKey;
    }

    public String getVersionId()
    {
        return versionId;
    }

}
