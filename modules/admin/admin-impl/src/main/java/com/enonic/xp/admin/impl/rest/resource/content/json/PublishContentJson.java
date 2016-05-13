package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.Set;

public class PublishContentJson
{
    private Set<String> ids;

    private Set<String> excludedIds;

    private boolean includeChildren;

    public boolean isIncludeChildren()
    {
        return includeChildren;
    }

    public void setIncludeChildren( final boolean includeChildren )
    {
        this.includeChildren = includeChildren;
    }

    public Set<String> getIds()
    {
        return ids;
    }

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
}