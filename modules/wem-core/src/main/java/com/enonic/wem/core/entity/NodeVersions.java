package com.enonic.wem.core.entity;

import java.util.Iterator;
import java.util.SortedSet;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

public class NodeVersions
    implements Iterable<NodeVersion>
{
    private final EntityId entityId;

    private final ImmutableSortedSet<NodeVersion> nodeVersions;

    private NodeVersions( Builder builder )
    {
        this.entityId = builder.entityId;
        this.nodeVersions = ImmutableSortedSet.copyOf( builder.nodeVersions );
    }

    public EntityId getEntityId()
    {
        return entityId;
    }

    public static Builder create( final EntityId entityId )
    {
        return new Builder( entityId );
    }

    @Override
    public Iterator<NodeVersion> iterator()
    {
        return nodeVersions.iterator();
    }

    public int size()
    {
        return nodeVersions.size();
    }

    public static final class Builder
    {
        private final SortedSet<NodeVersion> nodeVersions = Sets.newTreeSet();

        private final EntityId entityId;

        private Builder( final EntityId entityId )
        {
            this.entityId = entityId;
        }

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
