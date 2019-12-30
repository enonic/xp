package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class GetPublishStatusResult
{
    private final ContentId contentId;

    private final PublishStatus publishStatus;

    public GetPublishStatusResult( final ContentId contentId, final PublishStatus publishStatus )
    {
        this.contentId = contentId;
        this.publishStatus = publishStatus;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public PublishStatus getPublishStatus()
    {
        return publishStatus;
    }
}
