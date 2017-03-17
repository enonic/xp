package com.enonic.xp.content;

import com.google.common.annotations.Beta;

import com.enonic.xp.branch.Branch;

@Beta
public final class UndoPendingDeleteContentParams
{
    private final ContentIds contentIds;

    private final Branch target;

    public UndoPendingDeleteContentParams( final ContentIds contentIds, final Branch target )
    {
        this.contentIds = contentIds;
        this.target = target;
    }

    public ContentIds getContentIds()
    {
        return contentIds;
    }

    public Branch getBranch()
    {
        return target;
    }
}
