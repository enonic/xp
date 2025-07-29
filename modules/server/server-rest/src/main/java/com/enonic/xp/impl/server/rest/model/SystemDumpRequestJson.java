package com.enonic.xp.impl.server.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SystemDumpRequestJson
{
    private final String name;

    private final boolean includeVersions;

    private final boolean archive;

    private final Integer maxAge;

    private final Integer maxVersions;

    public SystemDumpRequestJson( @JsonProperty("name") final String name, //
                                  @JsonProperty("includeVersions") final boolean includeVersions, //
                                  @JsonProperty("maxAge") final Integer maxAge, //
                                  @JsonProperty("maxVersions") final Integer maxVersions, @JsonProperty("archive") final boolean archive )
    {
        this.name = name;
        this.maxAge = maxAge;
        this.maxVersions = maxVersions;
        this.includeVersions = includeVersions;
        this.archive = archive;
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

    public boolean isArchive()
    {
        return archive;
    }
}
