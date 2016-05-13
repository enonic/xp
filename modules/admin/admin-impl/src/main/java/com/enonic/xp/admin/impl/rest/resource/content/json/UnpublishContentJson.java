package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.Set;

public class UnpublishContentJson
{
    private Set<String> ids;

    public Set<String> getIds()
    {
        return ids;
    }

    @SuppressWarnings("unused")
    public void setIds( final Set<String> ids )
    {
        this.ids = ids;
    }
}
