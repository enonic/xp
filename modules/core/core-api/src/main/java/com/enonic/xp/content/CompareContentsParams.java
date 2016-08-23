package com.enonic.xp.content;

import com.google.common.annotations.Beta;

import com.enonic.xp.branch.BranchId;

@Beta
public class CompareContentsParams
{
    private final ContentIds contentIds;

    private final BranchId target;

    public CompareContentsParams( final ContentIds contentIds, final BranchId target )
    {
        this.contentIds = contentIds;
        this.target = target;
    }

    public ContentIds getContentIds()
    {
        return contentIds;
    }

    public BranchId getTarget()
    {
        return target;
    }
}
