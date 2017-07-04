package com.enonic.xp.repo.impl.dump.model;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.xp.node.NodeId;

public class VersionsDumpEntry
{
    private final NodeId nodeId;

    private final Set<VersionMeta> versions;

    private VersionsDumpEntry( final Builder builder )
    {
        nodeId = builder.nodeId;
        versions = builder.versions;
    }

    public static Builder create( final NodeId nodeId )
    {
        return new Builder( nodeId );
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public Set<VersionMeta> getVersions()
    {
        return versions;
    }

    public static final class Builder
    {
        private final NodeId nodeId;

        private Set<VersionMeta> versions = Sets.newHashSet();

        private Builder( final NodeId nodeId )
        {
            this.nodeId = nodeId;
        }

        public Builder versions( final Set<VersionMeta> val )
        {
            versions = val;
            return this;
        }

        public Builder addVersion( final VersionMeta versionMeta )
        {
            this.versions.add( versionMeta );
            return this;
        }

        public VersionsDumpEntry build()
        {
            return new VersionsDumpEntry( this );
        }
    }
}
