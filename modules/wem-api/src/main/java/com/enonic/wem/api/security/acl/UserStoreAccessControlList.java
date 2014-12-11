package com.enonic.wem.api.security.acl;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalKeys;

import static java.util.stream.Collectors.toSet;

public final class UserStoreAccessControlList
    implements Iterable<UserStoreAccessControlEntry>
{
    private final static UserStoreAccessControlList EMPTY = UserStoreAccessControlList.create().build();

    private final ImmutableMap<PrincipalKey, UserStoreAccessControlEntry> entries;

    private UserStoreAccessControlList( final Builder builder )
    {
        this.entries = ImmutableMap.copyOf( builder.entries );
    }

    public PrincipalKeys getAllPrincipals()
    {
        final Set<PrincipalKey> principals = this.entries.values().stream().
            map( UserStoreAccessControlEntry::getPrincipal ).
            collect( toSet() );
        return PrincipalKeys.from( principals );
    }

    public UserStoreAccessControlEntry getEntry( final PrincipalKey principalKey )
    {
        return this.entries.get( principalKey );
    }

    public boolean contains( final PrincipalKey principalKey )
    {
        return this.entries.containsKey( principalKey );
    }

    public boolean isEmpty()
    {
        return this.entries.isEmpty();
    }

    @Override
    public String toString()
    {
        return this.entries.values().toString();
    }

    @Override
    public Iterator<UserStoreAccessControlEntry> iterator()
    {
        return entries.values().iterator();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof UserStoreAccessControlList ) )
        {
            return false;
        }

        final UserStoreAccessControlList that = (UserStoreAccessControlList) o;
        return this.entries.equals( that.entries );
    }

    @Override
    public int hashCode()
    {
        return this.entries.hashCode();
    }

    public static UserStoreAccessControlList empty()
    {
        return EMPTY;
    }

    public static UserStoreAccessControlList of( final UserStoreAccessControlEntry... entries )
    {
        return UserStoreAccessControlList.create().addAll( entries ).build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final UserStoreAccessControlList acl )
    {
        return new Builder( acl );
    }

    public static class Builder
    {
        private final Map<PrincipalKey, UserStoreAccessControlEntry> entries;

        private Builder()
        {
            this.entries = Maps.newHashMap();
        }

        private Builder( final UserStoreAccessControlList acl )
        {
            this.entries = Maps.newHashMap();
            this.entries.putAll( acl.entries );
        }

        public Builder add( final UserStoreAccessControlEntry entry )
        {
            this.entries.put( entry.getPrincipal(), entry );
            return this;
        }

        public Builder addAll( final Iterable<UserStoreAccessControlEntry> entries )
        {
            for ( UserStoreAccessControlEntry entry : entries )
            {
                this.entries.put( entry.getPrincipal(), entry );
            }
            return this;
        }

        public Builder addAll( final UserStoreAccessControlEntry... entries )
        {
            for ( UserStoreAccessControlEntry entry : entries )
            {
                this.entries.put( entry.getPrincipal(), entry );
            }
            return this;
        }

        public Builder remove( final PrincipalKey principal )
        {
            this.entries.remove( principal );
            return this;
        }

        public UserStoreAccessControlList build()
        {
            return new UserStoreAccessControlList( this );
        }
    }

}
