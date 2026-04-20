package com.enonic.xp.repo.impl.dump.serializer.json;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.dump.model.BranchDumpEntry;

import static java.util.Objects.requireNonNullElse;

@JsonPropertyOrder(value = {"nodeId", "meta", "nodePath", "binaries"})
public class BranchDumpEntryJson
{
    @JsonProperty("nodeId")
    private String nodeId;

    @JsonProperty("meta")
    private VersionDumpEntryJson meta;

    @JsonProperty("binaries")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> binaries;

    @SuppressWarnings("unused")
    public BranchDumpEntryJson()
    {
    }

    private BranchDumpEntryJson( final String nodeId, final List<String> binaries, final VersionDumpEntryJson meta )
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
                                    requireNonNullElse( json.getBinaries(), List.of() ) );
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

    public List<String> getBinaries()
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

        private List<String> binaries;

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

        public Builder binaries( final List<String> binaries )
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
