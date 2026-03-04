package com.enonic.xp.impl.server.rest.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SystemLoadRequestJson
{
    private final String name;

    private final boolean upgrade;

    private final List<String> repositories;

    public SystemLoadRequestJson( @JsonProperty("name") final String name, @JsonProperty("upgrade") final boolean upgrade,
                                  @JsonProperty("repositories") final List<String> repositories )
    {
        this.name = name;
        this.upgrade = upgrade;
        this.repositories = repositories;
    }

    public String getName()
    {
        return name;
    }

    public boolean isUpgrade()
    {
        return upgrade;
    }

    public List<String> getRepositories()
    {
        return repositories;
    }
}
