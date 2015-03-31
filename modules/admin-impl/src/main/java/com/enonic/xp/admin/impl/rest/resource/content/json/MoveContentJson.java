package com.enonic.xp.admin.impl.rest.resource.content.json;


import java.util.List;

import com.enonic.xp.content.ContentPath;

public class MoveContentJson
{
    private List<String> contentIds;

    private ContentPath parentContentPath;

    public List<String> getContentIds()
    {
        return contentIds;
    }

    public ContentPath getParentContentPath()
    {
        return parentContentPath;
    }

    public void setContentIds( final List<String> contentIds )
    {
        this.contentIds =  contentIds ;
    }

    public void setParentContentPath( final String parentContentPath )
    {
        this.parentContentPath = ContentPath.from( parentContentPath );
    }
}
