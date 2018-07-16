package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.content.ContentPaths;

public class ContentPathsJson
{
    private ContentPaths contentPaths;

    public ContentPathsJson( @JsonProperty("contentPaths") final List<String> contentPaths )
    {
        this.contentPaths = ContentPaths.from( contentPaths );
    }

    public ContentPaths getContentPaths()
    {
        return contentPaths;
    }
}
