package com.enonic.xp.admin.impl.json.content;

import com.enonic.xp.content.CompareContentResult;

public class CompareContentResultJson
{
    private final String compareStatus;

    private final String id;

    private final PublishStatus publishStatus;

    public CompareContentResultJson( final CompareContentResult compareContentResult, final PublishStatus publishStatus )
    {
        this.compareStatus = compareContentResult.getCompareStatus().name();
        this.id = compareContentResult.getContentId().toString();
        this.publishStatus = publishStatus;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getCompareStatus()
    {
        return compareStatus;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getId()
    {
        return id;
    }

    public PublishStatus getPublishStatus()
    {
        return publishStatus;
    }
}
