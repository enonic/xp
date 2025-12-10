package com.enonic.xp.repo.impl.dump.serializer.json;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.dump.model.BranchDumpEntry;

public class BranchDumpEntryJson
{
    @JsonProperty("nodeId")
    private String nodeId;

    @JsonProperty("meta")
    private VersionDumpEntryJson meta;

    @JsonProperty("binaries")
    private Collection<String> binaries;

    @SuppressWarnings("unused")
    public BranchDumpEntryJson()
    {
    }

    private BranchDumpEntryJson( final String nodeId, final Collection<String> binaries, final VersionDumpEntryJson meta )
    {
        this.nodeId = nodeId;
        this.binaries = binaries;
        this.meta = meta;
    }

    private BranchDumpEntryJson( final Builder builder )
    {
        nodeId = builder.nodeId;
        meta = builder.meta;
        binaries = builder.binaries;
    }

    public static BranchDumpEntry fromJson( final BranchDumpEntryJson json )
    {
        return BranchDumpEntry.create().
            nodeId( NodeId.from( json.getNodeId() ) ).
            meta( VersionDumpEntryJson.fromJson( json.getMeta() ) ).setBinaryReferences(
            Objects.requireNonNullElse( json.getBinaries(), Collections.emptyList() ) ).
            build();
    }

    public static BranchDumpEntryJson from( final BranchDumpEntry branchDumpEntry )
    {
        String nodeId = branchDumpEntry.getNodeId().toString();
        return new BranchDumpEntryJson( nodeId, branchDumpEntry.getBinaryReferences(),
                                        VersionDumpEntryJson.from( branchDumpEntry.getMeta() ) );
    }

    public static Builder create( final BranchDumpEntryJson source )
    {
        return new Builder( source );
    }

    private Collection<String> getBinaries()
    {
        return binaries;
    }

    public VersionDumpEntryJson getMeta()
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

        private VersionDumpEntryJson meta;

        private Collection<String> binaries;

        private Builder()
        {
        }

        private Builder( final BranchDumpEntryJson source )
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

        public Builder meta( final VersionDumpEntryJson meta )
        {
            this.meta = meta;
            return this;
        }

        public Builder binaries( final Collection<String> binaries )
        {
            this.binaries = binaries;
            return this;
        }

        public BranchDumpEntryJson build()
        {
            return new BranchDumpEntryJson( this );
        }
    }
}
