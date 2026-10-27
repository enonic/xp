package com.enonic.xp.security.token;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleRestriction;
import com.enonic.xp.support.AbstractImmutableEntityList;

/**
 * The scopes of an id provider.
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
     * The restriction a set of approved scopes implies: none when the id provider has no scopes at
     * all, else exactly the union of the roles of the approved scopes among these. An id not among
     * these implies nothing.
     *
     * @param approved the ids of the scopes granted
     */
    public RoleRestriction restrictionFor( final Collection<String> approved )
    {
        if ( isEmpty() )
        {
            return RoleRestriction.none();
        }
        final Set<String> ids = Set.copyOf( approved );
        return RoleRestriction.to( stream()
                                       .filter( scope -> ids.contains( scope.getId() ) )
                                       .flatMap( scope -> scope.getRoles().stream() )
                                       .collect( PrincipalKeys.collector() ) );
    }
}
