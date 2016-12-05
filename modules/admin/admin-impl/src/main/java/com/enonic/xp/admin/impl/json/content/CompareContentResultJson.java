package com.enonic.xp.admin.impl.json.content;

import com.enonic.xp.content.CompareContentResult;
import com.enonic.xp.content.GetPublishStatusResult;

public class CompareContentResultJson
{
    private final String id;

    private final String compareStatus;

    private final String publishStatus;

    public CompareContentResultJson( final CompareContentResult compareContentResult, final GetPublishStatusResult getPublishStatusResult )
    {
        this.id = compareContentResult.getContentId().toString();
        this.compareStatus = compareContentResult.getCompareStatus().name();
        this.publishStatus = getPublishStatusResult == null || getPublishStatusResult.getPublishStatus() == null
            ? null
            : getPublishStatusResult.getPublishStatus().toString();
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getId()
    {
        return id;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getCompareStatus()
    {
        return compareStatus;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getPublishStatus()
    {
        return publishStatus;
    }
}
