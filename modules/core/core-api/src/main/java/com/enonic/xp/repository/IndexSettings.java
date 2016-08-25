package com.enonic.xp.repository;

import com.google.common.annotations.Beta;

@Beta
public class IndexSettings
{
    private final IndexResource resource;

    public IndexSettings( final IndexResource resource )
    {
        this.resource = resource;
    }

    public IndexResource getResource()
    {
        return resource;
    }

    public String getAsString()
    {
        return resource.getAsString();
    }
}
