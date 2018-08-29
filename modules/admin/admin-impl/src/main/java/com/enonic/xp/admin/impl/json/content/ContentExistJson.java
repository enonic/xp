package com.enonic.xp.admin.impl.json.content;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ContentExistJson
{

    private final String contentId;

    private final boolean exists;

    public ContentExistJson( final String contentId, final boolean exists )
    {
        this.contentId = contentId;
        this.exists = exists;
    }

    public String getContentId()
    {
        return contentId;
    }

    @JsonProperty("exists")
    @SuppressWarnings("UnusedDeclaration")
    public boolean exists()
    {
        return exists;
    }
}