package com.enonic.xp.node;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.enonic.xp.support.AbstractImmutableEntityList;

public class NodeVersions
    extends AbstractImmutableEntityList<NodeVersion>
{
    private NodeVersions( final Builder builder )
    {
        super( ImmutableList.copyOf( builder.nodeVersions ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        final List<NodeVersion> nodeVersions = Lists.newLinkedList();

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
