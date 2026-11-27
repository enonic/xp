package com.enonic.xp.security.token;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.support.AbstractImmutableEntityList;

/**
 * Grants of a user, newest first.
 */
@NullMarked
public final class Grants
    extends AbstractImmutableEntityList<Grant>
{
    private static final Grants EMPTY = new Grants( ImmutableList.of() );

    private Grants( final ImmutableList<Grant> list )
    {
        super( list );
    }

    /**
     * No grants.
     */
    public static Grants empty()
    {
        return EMPTY;
    }

    /**
     * The grants given, in that order.
     */
    public static Grants from( final Grant... grants )
    {
        return fromInternal( ImmutableList.copyOf( grants ) );
    }

    /**
     * The grants given, in that order.
     */
    public static Grants from( final Iterable<Grant> grants )
    {
        return grants instanceof Grants g ? g : fromInternal( ImmutableList.copyOf( grants ) );
    }

    /**
     * Collects a stream of grants.
     */
    public static Collector<Grant, ?, Grants> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), Grants::fromInternal );
    }

    private static Grants fromInternal( final ImmutableList<Grant> list )
    {
        return list.isEmpty() ? EMPTY : new Grants( list );
    }
}
