package com.enonic.wem.api.entity;

import java.util.Iterator;
import java.util.SortedSet;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

public class EntityVersions
    implements Iterable<EntityVersion>
{
    private final EntityId entityId;

    private final ImmutableSortedSet<EntityVersion> entityVersions;

    private EntityVersions( Builder builder )
    {
        this.entityId = builder.entityId;
        this.entityVersions = ImmutableSortedSet.copyOf( builder.entityVersions );
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
    public Iterator<EntityVersion> iterator()
    {
        return entityVersions.iterator();
    }

    public int size()
    {
        return entityVersions.size();
    }

    public static final class Builder
    {
        private SortedSet<EntityVersion> entityVersions = Sets.newTreeSet();

        private EntityId entityId;

        private Builder( final EntityId entityId )
        {
            this.entityId = entityId;
        }

        public Builder add( final EntityVersion entityVersion )
        {
            this.entityVersions.add( entityVersion );
            return this;
        }

        public EntityVersions build()
        {
            return new EntityVersions( this );
        }
    }
}
