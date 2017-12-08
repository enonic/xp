package com.enonic.xp.admin.impl.rest.resource.content.json;


import java.util.List;

public class DuplicateContentJson
{
    private List<String> contentIds;

    public List<String> getContentIds()
    {
        return contentIds;
    }

    public void setContentIds( final List<String> contentIds )
    {
        this.contentIds = contentIds;
    }
}
