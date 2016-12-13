package com.enonic.xp.content;

import com.google.common.annotations.Beta;

import com.enonic.xp.branch.Branch;

@Beta
public class GetPublishStatusesParams
{
    private final ContentIds contentIds;

    private final Branch target;

    public GetPublishStatusesParams( final ContentIds contentIds, final Branch target )
    {
        this.contentIds = contentIds;
        this.target = target;
    }

    public ContentIds getContentIds()
    {
        return contentIds;
    }

    public Branch getTarget()
    {
        return target;
    }
}
