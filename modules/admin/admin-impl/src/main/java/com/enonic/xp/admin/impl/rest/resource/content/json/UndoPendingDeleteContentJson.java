package com.enonic.xp.admin.impl.rest.resource.content.json;


import java.util.List;

public class UndoPendingDeleteContentJson
{
    private List<String> contentIds;

    public List<String> getContentIds()
    {
        return contentIds;
    }

    @SuppressWarnings("unused")
    public void setContentIds( final List<String> contentIds )
    {
        this.contentIds = contentIds;
    }
}
