package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.Set;

public class UnpublishContentJson
{
    private Set<String> ids;

    private boolean includeChildren;

    private boolean clearPublishInfo;

    public Set<String> getIds()
    {
        return ids;
    }

    public boolean isIncludeChildren()
    {
        return includeChildren;
    }

    public boolean isClearPublishInfo()
    {
        return clearPublishInfo;
    }

    @SuppressWarnings("unused")
    public void setIncludeChildren( final boolean includeChildren )
    {
        this.includeChildren = includeChildren;
    }

    @SuppressWarnings("unuser")
    public void setClearPublishInfo( final boolean clearPublishInfo )
    {
        this.clearPublishInfo = clearPublishInfo;
    }

    @SuppressWarnings("unused")
    public void setIds( final Set<String> ids )
    {
        this.ids = ids;
    }
}
