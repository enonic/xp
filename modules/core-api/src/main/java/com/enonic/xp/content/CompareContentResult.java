package com.enonic.xp.content;

import com.google.common.annotations.Beta;

@Beta
public class CompareContentResult
{
    private final ContentId contentId;

    private final CompareStatus compareStatus;

    public CompareContentResult( final CompareStatus compareStatus, final ContentId contentId )
    {
        this.compareStatus = compareStatus;
        this.contentId = contentId;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public CompareStatus getCompareStatus()
    {
        return compareStatus;
    }
}
