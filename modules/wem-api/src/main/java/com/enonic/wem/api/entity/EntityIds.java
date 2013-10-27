package com.enonic.wem.api.entity;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import com.enonic.wem.api.support.AbstractImmutableEntitySet;

public class EntityIds
    extends AbstractImmutableEntitySet<EntityId>
{

    private EntityIds( final ImmutableSet<EntityId> set )
    {
        super( set );
    }

    public static EntityIds empty()
    {
        final ImmutableSet<EntityId> set = ImmutableSet.of();
        return new EntityIds( set );
    }

    public static EntityIds from( final EntityId... ids )
    {
        return new EntityIds( ImmutableSet.copyOf( ids ) );
    }

    public static EntityIds from( final String... ids )
    {
        return new EntityIds( parseIds( ids ) );
    }

    public static EntityIds from( final Iterable<EntityId> ids )
    {
        return new EntityIds( ImmutableSet.copyOf( ids ) );
    }

    private static ImmutableSet<EntityId> parseIds( final String... paths )
    {
        final Collection<String> list = Lists.newArrayList( paths );
        final Collection<EntityId> pathList = Collections2.transform( list, new ParseFunction() );
        return ImmutableSet.copyOf( pathList );
    }

    private final static class ParseFunction
        implements Function<String, EntityId>
    {
        @Override
        public EntityId apply( final String value )
        {
            return EntityId.from( value );
        }
    }
}
