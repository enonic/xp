package com.enonic.wem.api.entity;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class EntityComparisons
    implements Iterable<EntityComparison>
{
    private final ImmutableSet<EntityComparison> entityComparisons;

    private EntityComparisons( Builder builder )
    {
        entityComparisons = ImmutableSet.copyOf( builder.entityComparisons );
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public Iterator<EntityComparison> iterator()
    {
        return entityComparisons.iterator();
    }

    public static final class Builder
    {
        private Set<EntityComparison> entityComparisons = Sets.newHashSet();

        private Builder()
        {
        }

        public Builder add( final EntityComparison entityComparison )
        {
            entityComparisons.add( entityComparison );
            return this;
        }

        public EntityComparisons build()
        {
            return new EntityComparisons( this );
        }
    }
}
