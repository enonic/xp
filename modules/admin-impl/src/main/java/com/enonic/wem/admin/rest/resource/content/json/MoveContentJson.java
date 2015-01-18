package com.enonic.wem.admin.rest.resource.content.json;


import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;

public class MoveContentJson
{
    private ContentId contentId;

    private ContentPath parentContentPath;

    public ContentId getContentId()
    {
        return contentId;
    }

    public ContentPath getParentContentPath()
    {
        return parentContentPath;
    }

    public void setContentId( final String contentId )
    {
        this.contentId = ContentId.from( contentId );
    }

    public void setParentContentPath( final String parentContentPath )
    {
        this.parentContentPath = ContentPath.from( parentContentPath );
    }
}
