package com.enonic.wem.api.command.content;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;

public class CreateContentResult
{

    private final ContentId contentId;

    private final ContentPath contentPath;

    public CreateContentResult( final ContentId contentId, final ContentPath contentPath )
    {
        this.contentId = contentId;
        this.contentPath = contentPath;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public ContentPath getContentPath()
    {
        return contentPath;
    }
}
