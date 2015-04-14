package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.Set;

public class CountItemsWithChildrenJson
{
    private Set<String> contentPaths;

    public Set<String> getContentPaths()
    {
        return contentPaths;
    }

    public void setContentPaths( final Set<String> contentPaths )
    {
        this.contentPaths = contentPaths;
    }
}
