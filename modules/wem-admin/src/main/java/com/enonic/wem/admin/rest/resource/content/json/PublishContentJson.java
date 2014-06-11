package com.enonic.wem.admin.rest.resource.content.json;

import com.enonic.wem.api.content.ContentId;

public class PublishContentJson
{
    private final ContentId contentId;

    public PublishContentJson( final ContentId contentId )
    {
        this.contentId = contentId;
    }


    public ContentId getContentId()
    {
        return contentId;
    }
}
