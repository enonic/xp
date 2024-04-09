package com.enonic.xp.security.acl;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;


@PublicApi
public final class AccessControlList
    implements Iterable<AccessControlEntry>
{
    private static final AccessControlList EMPTY = new AccessControlList( ImmutableMap.of() );

    private final ImmutableMap<PrincipalKey, AccessControlEntry> entries;

    private AccessControlList( final ImmutableMap<PrincipalKey, AccessControlEntry> entries )
    {
        this.entries = entries;
    }

    public static AccessControlList empty()
    {
        return EMPTY;
    }

    public static AccessControlList of( final AccessControlEntry... entries )
    {
        return AccessControlList.create().addAll( entries ).build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final AccessControlList acl )
    {
        return new Builder( acl );
    }

    public boolean isAllowedFor( final PrincipalKey principal, final Permission... permissions )
    {
        final AccessControlEntry entry = this.entries.get( principal );
        return entry != null && entry.isAllowed( permissions );
    }

    public boolean isAllowedFor( final PrincipalKey principal, final Permission permission )
    {
        final AccessControlEntry entry = this.entries.get( principal );
        return entry != null && entry.isAllowed( permission );
    }

    public boolean isAllowedFor( final PrincipalKeys principals, final Permission... permissions )
    {
        for ( final PrincipalKey principal : principals )
        {
            if ( isAllowedFor( principal, permissions ) )
            {
                return true;
            }
        }

        return false;
    }

    public boolean isAllowedFor( final PrincipalKeys principals, final Permission permission )
    {
        for ( final PrincipalKey principal : principals )
        {
            if ( isAllowedFor( principal, permission ) )
            {
                return true;
            }
        }

        return false;
    }

    public PrincipalKeys getAllPrincipals()
    {
        return PrincipalKeys.from( this.entries.keySet() );
    }

    public PrincipalKeys getPrincipalsWithPermission( final Permission permission )
    {
        return PrincipalKeys.from( this.entries.values()
                                       .stream()
                                       .filter( ( entry ) -> entry.isAllowed( permission ) )
                                       .map( AccessControlEntry::getPrincipal )
                                       .collect( ImmutableSet.toImmutableSet() ) );
    }

    public AccessControlEntry getEntry( final PrincipalKey principalKey )
    {
        return this.entries.get( principalKey );
    }

    public Collection<AccessControlEntry> getEntries()
    {
        return this.entries.values();
    }

    public Map<PrincipalKey, AccessControlEntry> asMap()
    {
        return this.entries;
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
    public Iterator<AccessControlEntry> iterator()
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
        if ( !( o instanceof AccessControlList ) )
        {
            return false;
        }

        final AccessControlList that = (AccessControlList) o;
        return this.entries.equals( that.entries );
    }

    @Override
    public int hashCode()
    {
        return this.entries.hashCode();
    }

    public static class Builder
    {
        private final Map<PrincipalKey, AccessControlEntry> entries;

        private Builder()
        {
            this.entries = new LinkedHashMap<>();
        }

        private Builder( final AccessControlList acl )
        {
            this.entries = new LinkedHashMap<>( acl.entries );
        }

        public Builder add( final AccessControlEntry entry )
        {
            this.entries.put( entry.getPrincipal(), entry );
            return this;
        }

        public Builder addAll( final Iterable<AccessControlEntry> entries )
        {
            for ( AccessControlEntry entry : entries )
            {
                this.entries.put( entry.getPrincipal(), entry );
            }
            return this;
        }

        public Builder addAll( final AccessControlEntry... entries )
        {
            for ( AccessControlEntry entry : entries )
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

        public AccessControlList build()
        {
            return new AccessControlList( ImmutableMap.copyOf( entries ) );
        }
    }

}
