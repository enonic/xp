package com.enonic.wem.api.content;

import com.enonic.wem.api.entity.CompareState;

public class ContentCompareResult
{

    private final ContentId contentId;

    private final CompareState compareState;


    public ContentCompareResult( final CompareState compareState, final ContentId contentId )
    {
        this.compareState = compareState;
        this.contentId = contentId;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public CompareState getCompareState()
    {
        return compareState;
    }
}
