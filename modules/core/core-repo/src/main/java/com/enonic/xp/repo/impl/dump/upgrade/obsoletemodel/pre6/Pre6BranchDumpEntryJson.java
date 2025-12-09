package com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre6;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.dump.model.BranchDumpEntry;

public class Pre6BranchDumpEntryJson
{
    @JsonProperty("nodeId")
    private String nodeId;

    @JsonProperty("meta")
    private Pre6VersionDumpEntryJson meta;

    @JsonProperty("binaries")
    private Collection<String> binaries;

    @SuppressWarnings("unused")
    public Pre6BranchDumpEntryJson()
    {
    }

    private Pre6BranchDumpEntryJson( final Builder builder )
    {
        nodeId = builder.nodeId;
        meta = builder.meta;
        binaries = builder.binaries;
    }

    public static BranchDumpEntry fromJson( final Pre6BranchDumpEntryJson json )
    {
        return BranchDumpEntry.create().
            nodeId( NodeId.from( json.getNodeId() ) ).
            meta( Pre6VersionDumpEntryJson.fromJson( json.getMeta() ) ).
            setBinaryReferences( json.getBinaries() ).
            build();
    }

    public static Builder create( final Pre6BranchDumpEntryJson source )
    {
        return new Builder( source );
    }

    private Collection<String> getBinaries()
    {
        return binaries;
    }

    public Pre6VersionDumpEntryJson getMeta()
    {
        return meta;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public String getNodeId()
    {
        return nodeId;
    }

    public static final class Builder
    {
        private String nodeId;

        private Pre6VersionDumpEntryJson meta;

        private Collection<String> binaries;

        private Builder()
        {
        }

        private Builder( final Pre6BranchDumpEntryJson source )
        {
            this.nodeId = source.getNodeId();
            this.binaries = source.getBinaries();
            this.meta = source.getMeta();
        }

        public Builder nodeId( final String nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder meta( final Pre6VersionDumpEntryJson meta )
        {
            this.meta = meta;
            return this;
        }

        public Builder binaries( final Collection<String> binaries )
        {
            this.binaries = binaries;
            return this;
        }

        public Pre6BranchDumpEntryJson build()
        {
            return new Pre6BranchDumpEntryJson( this );
        }
    }
}
