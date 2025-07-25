package com.enonic.xp.branch;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class Branches
    extends AbstractImmutableEntitySet<Branch>
{
    public static final Branches EMPTY = new Branches( ImmutableSet.of() );

    private Branches( final ImmutableSet<Branch> set )
    {
        super( set );
    }

    public static Branches from( final Branch... branches )
    {
        return fromInternal( ImmutableSet.copyOf( branches ) );
    }

    public static Branches from( final Iterable<Branch> branches )
    {
        return fromInternal( ImmutableSet.copyOf( branches ) );
    }

    public static Branches empty()
    {
        return EMPTY;
    }

    private static Branches fromInternal( final ImmutableSet<Branch> set )
    {
        return set.isEmpty() ? EMPTY : new Branches( set );
    }

    public static Collector<Branch, ?, Branches> collector()
    {
        return Collectors.collectingAndThen( ImmutableSet.toImmutableSet(), Branches::fromInternal );
    }
}
