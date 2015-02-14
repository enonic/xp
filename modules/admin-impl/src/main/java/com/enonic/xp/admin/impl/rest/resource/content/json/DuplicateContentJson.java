package com.enonic.xp.admin.impl.rest.resource.content.json;


import com.enonic.wem.api.content.ContentId;

public class DuplicateContentJson
{
    private ContentId contentId;

    public ContentId getContentId()
    {
        return contentId;
    }

    public void setContentId( final String contentId )
    {
        this.contentId = ContentId.from( contentId );
    }
}
