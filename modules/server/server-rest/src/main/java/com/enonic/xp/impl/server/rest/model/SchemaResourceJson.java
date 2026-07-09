package com.enonic.xp.impl.server.rest.model;

import java.time.Instant;

import com.enonic.xp.resource.Resource;

public class SchemaResourceJson
{
    private final String name;

    private final String type;

    private final String resource;

    private final String modifiedTime;

    public SchemaResourceJson( final String name, final String type, final Resource resource )
    {
        this.name = name;
        this.type = type;
        this.resource = resource.readString();
        this.modifiedTime = Instant.ofEpochMilli( resource.getTimestamp() ).toString();
    }

    public String getName()
    {
        return name;
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
