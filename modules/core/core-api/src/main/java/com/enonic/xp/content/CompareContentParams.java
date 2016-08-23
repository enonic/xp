package com.enonic.xp.content;

import com.google.common.annotations.Beta;

import com.enonic.xp.branch.BranchId;

@Beta
public class CompareContentParams
{
    private final ContentId contentId;

    private final BranchId target;

    public CompareContentParams( final ContentId contentId, final BranchId target )
    {
        this.contentId = contentId;
        this.target = target;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public BranchId getTarget()
    {
        return target;
    }
}
