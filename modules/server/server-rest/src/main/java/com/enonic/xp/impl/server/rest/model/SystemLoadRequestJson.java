package com.enonic.xp.impl.server.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SystemLoadRequestJson
{
    private final String name;

    private final boolean upgrade;

    private final boolean zip;

    public SystemLoadRequestJson( @JsonProperty("name") final String name, @JsonProperty("upgrade") final boolean upgrade,
                                  @JsonProperty("zip") final boolean zip )
    {
        this.name = name;
        this.upgrade = upgrade;
        this.zip = zip;
    }

    public String getName()
    {
        return name;
    }

    public boolean isUpgrade()
    {
        return upgrade;
    }

    public boolean isZip()
    {
        return zip;
    }
}
