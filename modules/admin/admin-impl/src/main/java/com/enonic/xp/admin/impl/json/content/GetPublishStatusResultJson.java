package com.enonic.xp.admin.impl.json.content;

import com.enonic.xp.content.ContentId;

public class GetPublishStatusResultJson
{
    private final PublishStatus publishStatus;

    private final String id;

    public GetPublishStatusResultJson( final PublishStatus publishStatus, final ContentId contentId )
    {
        this.publishStatus = publishStatus;
        this.id = contentId.toString();
    }

    @SuppressWarnings("UnusedDeclaration")
    public PublishStatus getPublishStatus()
    {
        return publishStatus;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getId()
    {
        return id;
    }
}
