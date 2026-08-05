package com.enonic.xp.impl.server.rest.model;

import java.time.Instant;

import com.enonic.xp.resource.Resource;

public class MacroResourceJson
{
    private final String key;

    private final String resource;

    private final String modifiedTime;

    public MacroResourceJson( final String key, final Resource resource )
    {
        this.key = key;
        this.resource = resource.readString();
        this.modifiedTime = Instant.ofEpochMilli( resource.getTimestamp() ).toString();
    }

    public String getKey()
    {
        return key;
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
