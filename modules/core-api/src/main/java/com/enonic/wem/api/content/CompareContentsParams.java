package com.enonic.wem.api.content;

import com.enonic.wem.api.workspace.Workspace;

public class CompareContentsParams
{
    private final ContentIds contentIds;

    private final Workspace target;

    public CompareContentsParams( final ContentIds contentIds, final Workspace target )
    {
        this.contentIds = contentIds;
        this.target = target;
    }

    public ContentIds getContentIds()
    {
        return contentIds;
    }

    public Workspace getTarget()
    {
        return target;
    }
}
