package com.enonic.xp.repo.impl.dump.serializer.json;

import java.util.Collection;
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

    private String getNodeId()
    {
        return nodeId;
    }

    public Collection<VersionDumpEntryJson> getVersions()
    {
        return versions;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private String nodeId;

        private Collection<VersionDumpEntryJson> versions;

        private Builder()
        {
        }

        public Builder nodeId( final String nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder versions( final Collection<VersionDumpEntryJson> versions )
        {
            this.versions = versions;
            return this;
        }

        public VersionsDumpEntryJson build()
        {
            return new VersionsDumpEntryJson( this );
        }
    }
}
