package com.enonic.xp.impl.server.rest.model;

import java.time.Instant;

import com.enonic.xp.resource.Resource;

public class ComponentResourceJson
{
    private final String key;

    private final String type;

    private final String resource;

    private final String modifiedTime;

    public ComponentResourceJson( final String key, final String type, final Resource resource )
    {
        this.key = key;
        this.type = type;
        this.resource = resource.readString();
        this.modifiedTime = Instant.ofEpochMilli( resource.getTimestamp() ).toString();
    }

    public String getKey()
    {
        return key;
    }

    public String getType()
    {
        return type;
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
