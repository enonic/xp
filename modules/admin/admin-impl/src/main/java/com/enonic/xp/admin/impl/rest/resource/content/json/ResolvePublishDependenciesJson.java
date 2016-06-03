package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.Set;

public class ResolvePublishDependenciesJson
{
    private Set<String> ids;

    private Set<String> excludedIds;

    private boolean includeChildren;

    private Integer from;

    private Integer size;

    public Set<String> getIds()
    {
        return ids;
    }

    @SuppressWarnings("unused")
    public void setIds( final Set<String> ids )
    {
        this.ids = ids;
    }

    public Set<String> getExcludedIds()
    {
        return excludedIds;
    }

    public void setExcludedIds( final Set<String> excludedIds )
    {
        this.excludedIds = excludedIds;
    }

    public boolean includeChildren()
    {
        return includeChildren;
    }

    @SuppressWarnings("unused")
    public void setIncludeChildren( final boolean includeChildren )
    {
        this.includeChildren = includeChildren;
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
