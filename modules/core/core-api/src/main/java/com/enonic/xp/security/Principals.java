package com.enonic.xp.security;

import java.util.function.Function;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.support.AbstractImmutableEntityList;

import static java.util.stream.Collectors.toList;

@Beta
public final class Principals
    extends AbstractImmutableEntityList<Principal>
{
    private final ImmutableMap<PrincipalKey, Principal> map;

    private Principals( final ImmutableList<Principal> list )
    {
        super( list );
        this.map = list.stream().collect( ImmutableMap.toImmutableMap( Principal::getKey, Function.identity() ) );
    }

    public PrincipalKeys getKeys()
    {
        return PrincipalKeys.from( map.keySet() );
    }

    public Principal getPrincipal( final PrincipalKey principalKey )
    {
        return map.get( principalKey );
    }

    public Iterable<User> getUsers()
    {
        return map.values().stream().
            filter( principal -> principal.getKey().isUser() ).
            map( principal -> (User) principal ).
            collect( toList() );
    }

    public Iterable<Group> getGroups()
    {
        return map.values().stream().
            filter( principal -> principal.getKey().isGroup() ).
            map( principal -> (Group) principal ).
            collect( toList() );
    }

    public Iterable<Role> getRoles()
    {
        return map.values().stream().
            filter( principal -> principal.getKey().isRole() ).
            map( principal -> (Role) principal ).
            collect( toList() );
    }

    @Override
    public String toString()
    {
        return this.list.toString();
    }

    public static Principals empty()
    {
        final ImmutableList<Principal> list = ImmutableList.of();
        return new Principals( list );
    }

    public static Principals from( final Principal... principals )
    {
        return new Principals( ImmutableList.copyOf( principals ) );
    }

    public static Principals from( final Iterable<? extends Principal> principals )
    {
        return new Principals( ImmutableList.copyOf( principals ) );
    }

}
