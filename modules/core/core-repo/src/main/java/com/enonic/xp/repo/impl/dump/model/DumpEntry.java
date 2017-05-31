package com.enonic.xp.repo.impl.dump.model;

import java.util.Collection;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersionId;

public class DumpEntry
{
    private final NodeId nodeId;

    private final Collection<Meta> versions;

    private DumpEntry( final Builder builder )
    {
        nodeId = builder.nodeId;
        versions = builder.otherVersions;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public Collection<Meta> getVersions()
    {
        return versions;
    }

    public Collection<NodeVersionId> getAllVersionIds()
    {
        return versions.stream().map( Meta::getVersion ).collect( Collectors.toList() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodeId nodeId;

        private Collection<Meta> otherVersions = Lists.newArrayList();

        private Builder()
        {
        }

        public Builder nodeId( final NodeId val )
        {
            nodeId = val;
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
