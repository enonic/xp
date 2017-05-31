package com.enonic.xp.repo.impl.dump.model;

import java.util.Collection;

import com.google.common.collect.Lists;

import com.enonic.xp.node.NodeId;

public class DumpEntry
{
    private final NodeId nodeId;

    private final Meta currentVersion;

    private final Collection<Meta> otherVersions;

    private DumpEntry( final Builder builder )
    {
        nodeId = builder.nodeId;
        currentVersion = builder.currentVersion;
        otherVersions = builder.otherVersions;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public Meta getCurrentVersion()
    {
        return currentVersion;
    }

    public Collection<Meta> getOtherVersions()
    {
        return otherVersions;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodeId nodeId;

        private Meta currentVersion;

        private Collection<Meta> otherVersions = Lists.newArrayList();

        private Builder()
        {
        }

        public Builder nodeId( final NodeId val )
        {
            nodeId = val;
            return this;
        }

        public Builder currentVersion( final Meta val )
        {
            currentVersion = val;
            return this;
        }

        public Builder setVersions( final Collection<Meta> values )
        {
            this.otherVersions = values;
            return this;
        }

        public Builder addVersion( final Meta val )
        {
            otherVersions.add( val );
            return this;
        }

        public DumpEntry build()
        {
            return new DumpEntry( this );
        }
    }
}
