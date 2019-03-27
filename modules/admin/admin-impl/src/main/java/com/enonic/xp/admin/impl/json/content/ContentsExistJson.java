package com.enonic.xp.admin.impl.json.content;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.content.ContentId;

public class ContentsExistJson
{
    private final List<ContentExistJson> contentsExistJson = Lists.newArrayList();

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
