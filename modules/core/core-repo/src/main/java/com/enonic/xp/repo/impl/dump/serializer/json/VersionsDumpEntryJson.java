package com.enonic.xp.repo.impl.dump.serializer.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.dump.model.VersionsDumpEntry;

@JsonPropertyOrder(value = {"nodeId", "versions"})
public class VersionsDumpEntryJson
{
    @JsonProperty("nodeId")
    private String nodeId;

    @JsonProperty("versions")
    private List<VersionDumpEntryJson> versions;

    @SuppressWarnings("unused")
    public VersionsDumpEntryJson()
    {
    }

    private VersionsDumpEntryJson( final String nodeId, final List<VersionDumpEntryJson> versions )
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
        return new VersionsDumpEntry( NodeId.from( json.getNodeId() ),
                                      json.getVersions().stream().map( VersionDumpEntryJson::fromJson ).toList() );
    }

    public static VersionsDumpEntryJson from( final VersionsDumpEntry entry )
    {
        String nodeId = entry.nodeId().toString();
        List<VersionDumpEntryJson> versions = entry.versions().stream().map( VersionDumpEntryJson::from ).toList();
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

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String nodeId;

        private final List<VersionDumpEntryJson> versions = new ArrayList<>();

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
