package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.Set;

public class ResolveDuplicateDependenciesJson
{
    private Set<String> ids;

    private Set<String> excludeChildrenIds;

    public Set<String> getIds()
    {
        return ids;
    }

    @SuppressWarnings("unused")
    public void setIds( final Set<String> ids )
    {
        this.ids = ids;
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
}
