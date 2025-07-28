package com.enonic.xp.security;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class PrincipalRelationships
    extends AbstractImmutableEntityList<PrincipalRelationship>
{
    private static final PrincipalRelationships EMPTY = new PrincipalRelationships( ImmutableList.of() );

    private PrincipalRelationships( final ImmutableList<PrincipalRelationship> list )
    {
        super( list );
    }

    public static PrincipalRelationships empty()
    {
        return EMPTY;
    }

    public static PrincipalRelationships from( final PrincipalRelationship... principalRelationships )
    {
        return fromInternal( ImmutableList.copyOf( principalRelationships ) );
    }

    public static PrincipalRelationships from( final Iterable<? extends PrincipalRelationship> principalRelationships )
    {
        return principalRelationships instanceof PrincipalRelationships p ? p : fromInternal( ImmutableList.copyOf( principalRelationships ) );
    }

    public static Collector<PrincipalRelationship, ?, PrincipalRelationships> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), PrincipalRelationships::fromInternal );
    }

    private static PrincipalRelationships fromInternal( final ImmutableList<PrincipalRelationship> list )
    {
        return list.isEmpty() ? EMPTY : new PrincipalRelationships( list );
    }
}
