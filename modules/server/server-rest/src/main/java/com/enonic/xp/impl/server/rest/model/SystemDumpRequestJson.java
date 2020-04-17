package com.enonic.xp.impl.server.rest.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SystemDumpRequestJson
{
    private final String name;

    private final boolean includeVersions;

    private final boolean zip;

    private final Integer maxAge;

    private final Integer maxVersions;

    public SystemDumpRequestJson( @JsonProperty("name") final String name, //
                                  @JsonProperty("includeVersions") final boolean includeVersions, //
                                  @JsonProperty("maxAge") final Integer maxAge, //
                                  @JsonProperty("maxVersions") final Integer maxVersions, @JsonProperty("zip") final boolean zip )
    {
        this.name = name;
        this.maxAge = maxAge;
        this.maxVersions = maxVersions;
        this.includeVersions = includeVersions;
        this.zip = zip;
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

    public boolean isZip()
    {
        return zip;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final SystemDumpRequestJson that = (SystemDumpRequestJson) o;
        return includeVersions == that.includeVersions && zip == that.zip && Objects.equals( name, that.name ) &&
            Objects.equals( maxAge, that.maxAge ) && Objects.equals( maxVersions, that.maxVersions );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, includeVersions, maxAge, maxVersions, zip );
    }
}
