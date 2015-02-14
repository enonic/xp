package com.enonic.xp.admin.impl.rest.resource.content.json;


import com.enonic.xp.core.content.ContentId;
import com.enonic.xp.core.content.ContentPath;

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
