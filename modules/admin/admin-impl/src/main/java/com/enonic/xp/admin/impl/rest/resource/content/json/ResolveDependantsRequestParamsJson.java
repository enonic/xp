package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.Set;

public class ResolveDependantsRequestParamsJson
{
    private Set<String> ids;

    private boolean includeChildren;

    public Set<String> getIds()
    {
        return ids;
    }

    @SuppressWarnings("unused")
    public void setIds( final Set<String> ids )
    {
        this.ids = ids;
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
}
