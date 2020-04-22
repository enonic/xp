package com.enonic.xp.impl.server.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SystemLoadRequestJson
{
    private final String name;

    private final boolean upgrade;

    private final boolean archive;

    public SystemLoadRequestJson( @JsonProperty("name") final String name, @JsonProperty("upgrade") final boolean upgrade,
                                  @JsonProperty("archive") final boolean archive )
    {
        this.name = name;
        this.upgrade = upgrade;
        this.archive = archive;
    }

    public String getName()
    {
        return name;
    }

    public boolean isUpgrade()
    {
        return upgrade;
    }

    public boolean isArchive()
    {
        return archive;
    }
}
