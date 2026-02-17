package com.enonic.xp.impl.server.rest.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SystemDumpRequestJson
{
    private final String name;

    private final boolean includeVersions;

    private final Integer maxAge;

    private final Integer maxVersions;

    private final List<String> repositories;

    public SystemDumpRequestJson( @JsonProperty("name") final String name, //
                                  @JsonProperty("includeVersions") final boolean includeVersions, //
                                  @JsonProperty("maxAge") final Integer maxAge, //
                                  @JsonProperty("maxVersions") final Integer maxVersions, //
                                  @JsonProperty("repositories") final List<String> repositories )
    {
        this.name = name;
        this.maxAge = maxAge;
        this.maxVersions = maxVersions;
        this.includeVersions = includeVersions;
        this.repositories = repositories;
    }

    public String getName()
    {
        return name;
    }

    public boolean isIncludeVersions()
    {
        return includeVersions;
    }

    public Integer getMaxAge()
    {
        return maxAge;
    }

    public Integer getMaxVersions()
    {
        return maxVersions;
    }

    public List<String> getRepositories()
    {
        return repositories;
    }
}
