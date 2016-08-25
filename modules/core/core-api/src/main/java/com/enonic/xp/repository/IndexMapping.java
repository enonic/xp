package com.enonic.xp.repository;

public class IndexMapping
{
    private final IndexResource resource;

    public IndexMapping( final IndexResource resource )
    {
        this.resource = resource;
    }

    public IndexResource getResource()
    {
        return resource;
    }

    public String getAsString()
    {
        return this.resource.getAsString();
    }
}
