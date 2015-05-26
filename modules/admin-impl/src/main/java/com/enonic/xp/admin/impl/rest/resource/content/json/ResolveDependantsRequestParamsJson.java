package com.enonic.xp.admin.impl.rest.resource.content.json;

public class ResolveDependantsRequestParamsJson
{
    private String id;

    private boolean includeChildren;

    public String getId()
    {
        return id;
    }

    public void setId( final String id )
    {
        this.id = id;
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
