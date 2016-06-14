package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.Set;

import com.enonic.xp.content.CompareStatus;

public class GetDescendantsOfContents
{
    private Set<String> contentPaths;

    private Set<CompareStatus> filterStatuses;

    private Integer from;

    private Integer size;

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

    public Integer getFrom()
    {
        return from;
    }

    public void setFrom( final Integer from )
    {
        this.from = from;
    }

    public Integer getSize()
    {
        return size;
    }

    public void setSize( final Integer size )
    {
        this.size = size;
    }


}
