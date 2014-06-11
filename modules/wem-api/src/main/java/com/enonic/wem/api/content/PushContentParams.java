package com.enonic.wem.api.content;

import com.enonic.wem.api.entity.Workspace;

public class PushContentParams
{

    private final ContentId contentId;

    private final Workspace to;

    public PushContentParams( final Workspace to, final ContentId contentId )
    {
        this.to = to;
        this.contentId = contentId;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public Workspace getTo()
    {
        return to;
    }
}
