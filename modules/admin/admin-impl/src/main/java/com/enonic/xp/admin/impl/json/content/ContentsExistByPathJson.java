package com.enonic.xp.admin.impl.json.content;

import java.util.List;

import org.codehaus.jparsec.util.Lists;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.content.ContentPath;

public class ContentsExistByPathJson
{
    private final List<ContentExistByPathJson> contentsExistJson = Lists.arrayList();

    public ContentsExistByPathJson()
    {
    }

    public void add( final ContentPath contentPath, final boolean exists )
    {
        contentsExistJson.add( new ContentExistByPathJson( contentPath.toString(), exists ) );
    }

    @SuppressWarnings("UnusedDeclaration")
    public List<ContentExistByPathJson> getContentsExistJson()
    {
        return contentsExistJson;
    }
}

class ContentExistByPathJson
{

    private final String contentPath;

    private final boolean exists;

    public ContentExistByPathJson( final String contentPath, final boolean exists )
    {
        this.contentPath = contentPath;
        this.exists = exists;
    }

    public String getContentPath()
    {
        return contentPath;
    }

    @JsonProperty("exists")
    @SuppressWarnings("UnusedDeclaration")
    public boolean exists()
    {
        return exists;
    }
}