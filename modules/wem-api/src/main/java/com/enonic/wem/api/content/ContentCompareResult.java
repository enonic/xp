package com.enonic.wem.api.content;

import com.enonic.wem.api.entity.CompareStatus;

public class ContentCompareResult
{

    private final ContentId contentId;

    private final CompareStatus compareStatus;


    public ContentCompareResult( final CompareStatus compareStatus, final ContentId contentId )
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
