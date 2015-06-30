package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.Set;

public class PublishContentJson
{
    private Set<String> ids;

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
}