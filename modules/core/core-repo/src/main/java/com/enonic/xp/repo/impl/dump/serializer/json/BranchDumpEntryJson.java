package com.enonic.xp.repo.impl.dump.serializer.json;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.dump.model.BranchDumpEntry;

@JsonPropertyOrder(value = {"nodeId", "meta", "nodePath", "binaries"})
public class BranchDumpEntryJson
{
    @JsonProperty("nodeId")
    private String nodeId;

    @JsonProperty("meta")
    private VersionDumpEntryJson meta;

    @JsonProperty("binaries")
    @JsonInclude(JsonInclude.Include.NON_NULL)
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
        return new BranchDumpEntry( NodeId.from( json.getNodeId() ), VersionDumpEntryJson.fromJson( json.getMeta() ),
                                    Objects.requireNonNullElse( json.getBinaries(), Collections.emptyList() ) );
    }

    public static BranchDumpEntryJson from( final BranchDumpEntry branchDumpEntry )
    {
        String nodeId = branchDumpEntry.nodeId().toString();
        return new BranchDumpEntryJson( nodeId, branchDumpEntry.binaryReferences(), VersionDumpEntryJson.from( branchDumpEntry.meta() ) );
    }

    public static Builder create( final BranchDumpEntryJson source )
    {
        return new Builder( source );
    }

    public Collection<String> getBinaries()
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
