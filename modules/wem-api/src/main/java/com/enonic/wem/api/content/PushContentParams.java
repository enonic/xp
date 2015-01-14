package com.enonic.wem.api.content;

import com.enonic.wem.api.workspace.Workspace;

public class PushContentParams
{
    private final ContentIds contentIds;

    private final Workspace target;

    public PushContentParams( final Workspace target, final ContentIds contentIds )
    {
        this.target = target;
        this.contentIds = contentIds;
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
