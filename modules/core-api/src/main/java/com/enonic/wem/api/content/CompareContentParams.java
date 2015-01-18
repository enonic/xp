package com.enonic.wem.api.content;

import com.enonic.wem.api.workspace.Workspace;

public class CompareContentParams
{
    private final ContentId contentId;

    private final Workspace target;

    public CompareContentParams( final ContentId contentId, final Workspace target )
    {
        this.contentId = contentId;
        this.target = target;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public Workspace getTarget()
    {
        return target;
    }
}
