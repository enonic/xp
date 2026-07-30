package com.enonic.xp.impl.server.rest.model;

import java.time.Instant;

import com.enonic.xp.resource.Resource;

public class PhrasesResourceJson
{
    private final String application;

    private final String name;

    private final String resource;

    private final String modifiedTime;

    public PhrasesResourceJson( final String application, final Resource resource )
    {
        this.application = application;
        this.name = resource.getKey().getName();
        this.resource = resource.readString();
        this.modifiedTime = Instant.ofEpochMilli( resource.getTimestamp() ).toString();
    }

    public String getApplication()
    {
        return application;
    }

    public String getName()
    {
        return name;
    }

    public String getResource()
    {
        return resource;
    }

    public String getModifiedTime()
    {
        return modifiedTime;
    }
}
