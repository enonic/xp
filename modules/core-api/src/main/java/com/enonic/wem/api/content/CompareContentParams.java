package com.enonic.wem.api.content;

import com.enonic.wem.api.branch.Branch;

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
