package com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre6;

import java.util.Collection;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.dump.model.VersionsDumpEntry;

public class Pre6VersionsDumpEntryJson
{
    @JsonProperty("nodeId")
    private String nodeId;

    @JsonProperty("versions")
    private Collection<Pre6VersionDumpEntryJson> versions;

    @SuppressWarnings("unused")
    public Pre6VersionsDumpEntryJson()
    {
    }

    private Pre6VersionsDumpEntryJson( final String nodeId, final Collection<Pre6VersionDumpEntryJson> versions )
    {
        this.nodeId = nodeId;
        this.versions = versions;
    }

    private Pre6VersionsDumpEntryJson( final Builder builder )
    {
        nodeId = builder.nodeId;
        versions = builder.versions;
    }

    public static VersionsDumpEntry fromJson( final Pre6VersionsDumpEntryJson json )
    {
        return VersionsDumpEntry.create( NodeId.from( json.getNodeId() ) ).
            versions( json.getVersions().stream().map( Pre6VersionDumpEntryJson::fromJson ).collect( Collectors.toSet() ) ).
            build();
    }

    public String getNodeId()
    {
        return nodeId;
    }

    public Collection<Pre6VersionDumpEntryJson> getVersions()
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

        private Collection<Pre6VersionDumpEntryJson> versions;

        private Builder()
        {
        }

        public Builder nodeId( final String nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder versions( final Collection<Pre6VersionDumpEntryJson> versions )
        {
            this.versions = versions;
            return this;
        }

        public Pre6VersionsDumpEntryJson build()
        {
            return new Pre6VersionsDumpEntryJson( this );
        }
    }
}
