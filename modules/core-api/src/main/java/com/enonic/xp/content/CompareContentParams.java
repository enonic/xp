package com.enonic.xp.content;

import com.google.common.annotations.Beta;

import com.enonic.xp.branch.Branch;

@Beta
public class CompareContentParams
{
    private final ContentId contentId;

    private final Branch target;

    public CompareContentParams( final ContentId contentId, final Branch target )
    {
        this.contentId = contentId;
        this.target = target;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public Branch getTarget()
    {
        return target;
    }
}
