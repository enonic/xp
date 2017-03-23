package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.Set;

public class ResolvePublishDependenciesJson
{
    private Set<String> ids;

    private Set<String> excludedIds;

    private Set<String> excludeChildrenIds;

    private boolean includeOffline;

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

    public Set<String> getExcludeChildrenIds()
    {
        return excludeChildrenIds;
    }

    @SuppressWarnings("unused")
    public void setExcludeChildrenIds( final Set<String> excludeChildrenIds )
    {
        this.excludeChildrenIds = excludeChildrenIds;
    }

    public boolean isIncludeOffline()
    {
        return includeOffline;
    }

    public void setIncludeOffline( final boolean includeOffline )
    {
        this.includeOffline = includeOffline;
    }
}