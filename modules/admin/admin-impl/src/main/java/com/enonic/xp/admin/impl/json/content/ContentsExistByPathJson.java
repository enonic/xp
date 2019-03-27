package com.enonic.xp.admin.impl.json.content;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.content.ContentPath;

public class ContentsExistByPathJson
{
    private final List<ContentExistByPathJson> contentsExistJson = Lists.newArrayList();

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
