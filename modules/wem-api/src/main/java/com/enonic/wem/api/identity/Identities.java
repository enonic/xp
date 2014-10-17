package com.enonic.wem.api.identity;

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

import static java.util.stream.Collectors.toList;

public final class Identities
    extends AbstractImmutableEntityList<Identity>
{
    private final ImmutableMap<IdentityKey, Identity> map;

    private Identities( final ImmutableList<Identity> list )
    {
        super( list );
        this.map = Maps.uniqueIndex( list, Identity::getIdentityKey );
    }

    public IdentityKeys getKeys()
    {
        return IdentityKeys.from( map.keySet() );
    }

    public Identity getIdentity( final IdentityKey IdentityKey )
    {
        return map.get( IdentityKey );
    }

    public Iterable<User> getUsers()
    {
        return map.values().stream().
            filter( identity -> identity.getIdentityKey().isUser() ).
            map( identity -> (User) identity ).
            collect( toList() );
    }

    public Iterable<Group> getGroups()
    {
        return map.values().stream().
            filter( identity -> identity.getIdentityKey().isGroup() ).
            map( identity -> (Group) identity ).
            collect( toList() );
    }

    public Iterable<Account> getAccounts()
    {
        return map.values().stream().
            filter( identity -> identity.getIdentityKey().isAccount() ).
            map( identity -> (Account) identity ).
            collect( toList() );
    }

    public Iterable<Agent> getAgents()
    {
        return map.values().stream().
            filter( identity -> identity.getIdentityKey().isAgent() ).
            map( identity -> (Agent) identity ).
            collect( toList() );
    }

    @Override
    public String toString()
    {
        return this.list.toString();
    }

    public static Identities empty()
    {
        final ImmutableList<Identity> list = ImmutableList.of();
        return new Identities( list );
    }

    public static Identities from( final Identity... identities )
    {
        return new Identities( ImmutableList.copyOf( identities ) );
    }

    public static Identities from( final Iterable<? extends Identity> identities )
    {
        return new Identities( ImmutableList.copyOf( identities ) );
    }

    public static Identities from( final Collection<? extends Identity> identities )
    {
        return new Identities( ImmutableList.copyOf( identities ) );
    }

}
