package com.enonic.xp.repo.impl.dump.serializer.json;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.dump.model.VersionsDumpEntry;

public class VersionsDumpEntryJson
{
    @JsonProperty("nodeId")
    private String nodeId;

    @JsonProperty("versions")
    private Collection<VersionDumpEntryJson> versions;

    @SuppressWarnings("unused")
    public VersionsDumpEntryJson()
    {
    }

    private VersionsDumpEntryJson( final String nodeId, final Collection<VersionDumpEntryJson> versions )
    {
        this.nodeId = nodeId;
        this.versions = versions;
    }

    private VersionsDumpEntryJson( final Builder builder )
    {
        nodeId = builder.nodeId;
        versions = builder.versions;
    }

    public static VersionsDumpEntry fromJson( final VersionsDumpEntryJson json )
    {
        return VersionsDumpEntry.create( NodeId.from( json.getNodeId() ) ).
            versions( json.getVersions().stream().map( VersionDumpEntryJson::fromJson ).collect( Collectors.toSet() ) ).
            build();
    }

    public static VersionsDumpEntryJson from( final VersionsDumpEntry entry )
    {
        String nodeId = entry.getNodeId().toString();
        Collection<VersionDumpEntryJson> versions =
            entry.getVersions().stream().map( VersionDumpEntryJson::from ).collect( Collectors.toSet() );
        return new VersionsDumpEntryJson( nodeId, versions );
    }

    public String getNodeId()
    {
        return nodeId;
    }

    public Collection<VersionDumpEntryJson> getVersions()
    {
        return versions;
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
        final VersionsDumpEntryJson that = (VersionsDumpEntryJson) o;
        return Objects.equals( nodeId, that.nodeId ) && Objects.equals( versions, that.versions );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( nodeId, versions );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String nodeId;

        private Collection<VersionDumpEntryJson> versions = new LinkedList<>();

        private Builder()
        {
        }

        public Builder nodeId( final String nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder version( final VersionDumpEntryJson version )
        {
            this.versions.add( version );
            return this;
        }

        public Builder versions( final Collection<VersionDumpEntryJson> versions )
        {
            this.versions.addAll( versions );
            return this;
        }

        public VersionsDumpEntryJson build()
        {
            return new VersionsDumpEntryJson( this );
        }
    }
}
