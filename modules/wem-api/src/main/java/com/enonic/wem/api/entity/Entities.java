package com.enonic.wem.api.entity;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

public final class Entities
    extends AbstractImmutableEntityList<Entity>
{
    private Entities( final ImmutableList<Entity> list )
    {
        super( list );
    }

    public EntityIds getIds()
    {
        final Collection<EntityId> paths = Collections2.transform( this.list, new ToKeyFunction() );
        return EntityIds.from( paths );
    }

    public static Entities empty()
    {
        final ImmutableList<Entity> list = ImmutableList.of();
        return new Entities( list );
    }

    public static Entities from( final Entity... entities )
    {
        return new Entities( ImmutableList.copyOf( entities ) );
    }

    public static Entities from( final Iterable<? extends Entity> entities )
    {
        return new Entities( ImmutableList.copyOf( entities ) );
    }

    public static Entities from( final Collection<? extends Entity> entities )
    {
        return new Entities( ImmutableList.copyOf( entities ) );
    }

    private final static class ToKeyFunction
        implements Function<Entity, EntityId>
    {
        @Override
        public EntityId apply( final Entity value )
        {
            return value.id();
        }
    }

    public static Builder newEntities()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ImmutableList.Builder<Entity> builder = ImmutableList.builder();

        public Builder add( Entity entity )
        {
            builder.add( entity );
            return this;
        }

        public Entities build()
        {
            return new Entities( builder.build() );
        }
    }
}
