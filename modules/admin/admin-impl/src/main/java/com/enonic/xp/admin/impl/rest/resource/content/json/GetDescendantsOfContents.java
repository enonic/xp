package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.Set;

import com.enonic.xp.content.CompareStatus;

public class GetDescendantsOfContents
{
    private Set<String> contentPaths;

    private Set<CompareStatus> filterStatuses;

    public Set<String> getContentPaths()
    {
        return contentPaths;
    }

    public void setContentPaths( final Set<String> contentPaths )
    {
        this.contentPaths = contentPaths;
    }

    public Set<CompareStatus> getFilterStatuses()
    {
        return filterStatuses;
    }

    public void setFilterStatuses( final Set<CompareStatus> filterStatuses )
    {
        this.filterStatuses = filterStatuses;
    }

}
