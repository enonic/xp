package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.content.ContentIds;

public class ContentIdsJson
{
    private final ContentIds contentIds;

    public ContentIdsJson( @JsonProperty("contentIds") final List<String> contentIds )
    {
        this.contentIds = ContentIds.from( contentIds );
    }

    public ContentIds getContentIds()
    {
        return contentIds;
    }
}
