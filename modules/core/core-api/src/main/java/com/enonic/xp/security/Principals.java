package com.enonic.xp.security;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class Principals
    extends AbstractImmutableEntityList<Principal>
{
    private static final Principals EMPTY = new Principals( ImmutableList.of() );

    private Principals( final ImmutableList<Principal> list )
    {
        super( list );
    }

    public PrincipalKeys getKeys()
    {
        return list.stream().map( Principal::getKey ).collect( PrincipalKeys.collector() );
    }

    public Principal getPrincipal( final PrincipalKey principalKey )
    {
        return list.stream().filter( p -> principalKey.equals( p.getKey() ) ).findFirst().orElse( null );
    }

    public Iterable<User> getUsers()
    {
        return list.stream().filter( p -> p.getKey().isUser() ).map( p -> (User) p ).collect( ImmutableSet.toImmutableSet() );
    }

    public Iterable<Group> getGroups()
    {
        return list.stream().filter( p -> p.getKey().isGroup() ).map( p -> (Group) p ).collect( ImmutableSet.toImmutableSet() );
    }

    public Iterable<Role> getRoles()
    {
        return list.stream().filter( p -> p.getKey().isRole() ).map( p -> (Role) p ).collect( ImmutableSet.toImmutableSet() );
    }

    public static Principals empty()
    {
        return EMPTY;
    }

    public static Principals from( final Principal... principals )
    {
        return fromInternal( ImmutableList.copyOf( principals ) );
    }

    public static Principals from( final Iterable<? extends Principal> principals )
    {
        return fromInternal( ImmutableList.copyOf( principals ) );
    }

    public static Collector<Principal, ?, Principals> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), Principals::fromInternal );
    }

    private static Principals fromInternal( final ImmutableList<Principal> list )
    {
        return new Principals( ImmutableList.copyOf( list ) );
    }
}
