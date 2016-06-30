package com.enonic.xp.admin.impl.json.content;

import java.util.List;

import org.codehaus.jparsec.util.Lists;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.content.ContentId;

public class ContentsExistJson
{
    private final List<ContentExistJson> contentsExistJson = Lists.arrayList();

    public ContentsExistJson()
    {
    }

    public void add( final ContentId contentId, final boolean exists )
    {
        contentsExistJson.add( new ContentExistJson( contentId.toString(), exists ) );
    }

    @SuppressWarnings("UnusedDeclaration")
    public List<ContentExistJson> getContentsExistJson()
    {
        return contentsExistJson;
    }
}

class ContentExistJson
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