package com.enonic.xp.security.token;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.support.AbstractImmutableEntityList;

/**
 * A list of scopes, such as those an application defines or all that are defined here.
 */
@NullMarked
public final class Scopes
    extends AbstractImmutableEntityList<Scope>
{
    private static final Scopes EMPTY = new Scopes( ImmutableList.of() );

    private Scopes( final ImmutableList<Scope> list )
    {
        super( list );
    }

    /**
     * No scopes.
     */
    public static Scopes empty()
    {
        return EMPTY;
    }

    /**
     * The scopes given, in that order.
     */
    public static Scopes from( final Scope... scopes )
    {
        return fromInternal( ImmutableList.copyOf( scopes ) );
    }

    /**
     * The scopes given, in that order.
     */
    public static Scopes from( final Iterable<Scope> scopes )
    {
        return scopes instanceof Scopes s ? s : fromInternal( ImmutableList.copyOf( scopes ) );
    }

    /**
     * Collects a stream of scopes.
     */
    public static Collector<Scope, ?, Scopes> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), Scopes::fromInternal );
    }

    private static Scopes fromInternal( final ImmutableList<Scope> list )
    {
        return list.isEmpty() ? EMPTY : new Scopes( list );
    }

    /**
     * The scope with the given id.
     */
    public Optional<Scope> get( final String id )
    {
        return stream().filter( scope -> scope.getId().equals( id ) ).findFirst();
    }

    /**
     * The ids, in order.
     */
    public Set<String> getIds()
    {
        return stream().map( Scope::getId ).collect( ImmutableSet.toImmutableSet() );
    }

    /**
     * The scopes a token granted the given ones passes for: those, and all that they include among
     * these, directly or through others. An id not among these stands for itself alone.
     *
     * @param granted the ids of the scopes granted
     */
    public Set<String> expand( final Collection<String> granted )
    {
        final Set<String> expanded = new LinkedHashSet<>();
        final Deque<String> pending = new ArrayDeque<>( granted );
        while ( !pending.isEmpty() )
        {
            final String id = pending.pop();
            if ( expanded.add( id ) )
            {
                get( id ).ifPresent( scope -> pending.addAll( scope.getIncludes() ) );
            }
        }
        return expanded;
    }
}
