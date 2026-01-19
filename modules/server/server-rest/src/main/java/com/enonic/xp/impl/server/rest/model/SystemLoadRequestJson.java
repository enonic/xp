package com.enonic.xp.impl.server.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SystemLoadRequestJson
{
    private final String name;

    private final boolean upgrade;

    public SystemLoadRequestJson( @JsonProperty("name") final String name, //
                                  @JsonProperty("upgrade") final boolean upgrade )
    {
        this.name = name;
        this.upgrade = upgrade;
    }

    public String getName()
    {
        return name;
    }

    public boolean isUpgrade()
    {
        return upgrade;
    }
}
