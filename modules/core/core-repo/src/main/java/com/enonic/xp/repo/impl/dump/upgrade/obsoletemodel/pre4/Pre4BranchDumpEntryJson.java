package com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre4;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Pre4BranchDumpEntryJson
{
    @JsonProperty("nodeId")
    private String nodeId;

    @JsonProperty("meta")
    private Pre4VersionDumpEntryJson meta;

    @JsonProperty("binaries")
    private Collection<String> binaries;

    @SuppressWarnings("unused")
    public Pre4BranchDumpEntryJson()
    {
    }

    private Pre4BranchDumpEntryJson( final Builder builder )
    {
        nodeId = builder.nodeId;
        meta = builder.meta;
        binaries = builder.binaries;
    }

    public String getNodeId()
    {
        return nodeId;
    }

    public Collection<String> getBinaries()
    {
        return binaries;
    }

    public Pre4VersionDumpEntryJson getMeta()
    {
        return meta;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String nodeId;

        private Pre4VersionDumpEntryJson meta;

        private Collection<String> binaries;

        private Builder()
        {
        }

        public Builder nodeId( final String nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder meta( final Pre4VersionDumpEntryJson meta )
        {
            this.meta = meta;
            return this;
        }

        public Builder binaries( final Collection<String> binaries )
        {
            this.binaries = binaries;
            return this;
        }

        public Pre4BranchDumpEntryJson build()
        {
            return new Pre4BranchDumpEntryJson( this );
        }
    }
}
