package com.enonic.xp.node;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

public final class NodeVersions
    extends AbstractImmutableEntityList<NodeVersion>
{
    private NodeVersions( final Builder builder )
    {
        super( builder.nodeVersions.build() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final ImmutableList.Builder<NodeVersion> nodeVersions = ImmutableList.builder();

        public Builder add( final NodeVersion nodeVersion )
        {
            this.nodeVersions.add( nodeVersion );
            return this;
        }

        public NodeVersions build()
        {
            return new NodeVersions( this );
        }

    }

}
