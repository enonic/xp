package com.enonic.xp.security.acl;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

import static java.util.stream.Collectors.toSet;

@Beta
public final class AccessControlList
    implements Iterable<AccessControlEntry>
{
    private final static AccessControlList EMPTY = AccessControlList.create().build();

    private final ImmutableMap<PrincipalKey, AccessControlEntry> entries;

    private AccessControlList( final Builder builder )
    {
        this.entries = ImmutableMap.copyOf( builder.entries );
    }

    public boolean isAllowedFor( final PrincipalKey principal, final Permission... permissions )
    {
        return doIsAllowedFor( principal, permissions );
    }

    public boolean isAllowedFor( final PrincipalKeys principals, final Permission... permissions )
    {
        for ( final PrincipalKey principal : principals )
        {
            if ( doIsAllowedFor( principal, permissions ) )
            {
                return true;
            }
        }

        return false;
    }

    private boolean doIsAllowedFor( final PrincipalKey principal, final Permission[] permissions )
    {
        final AccessControlEntry entry = this.entries.get( principal );
        return entry != null && entry.isAllowed( permissions );
    }


    public PrincipalKeys getAllPrincipals()
    {
        final Set<PrincipalKey> principals = this.entries.values().stream().
            map( AccessControlEntry::getPrincipal ).
            collect( toSet() );
        return PrincipalKeys.from( principals );
    }

    public PrincipalKeys getPrincipalsWithPermission( final Permission permission )
    {
        final Set<PrincipalKey> principals = this.entries.values().stream().
            filter( ( entry ) -> entry.isAllowed( permission ) ).
            map( AccessControlEntry::getPrincipal ).
            collect( toSet() );
        return PrincipalKeys.from( principals );
    }

    public AccessControlEntry getEntry( final PrincipalKey principalKey )
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

    public static class Builder
    {
        private final Map<PrincipalKey, AccessControlEntry> entries;

        private Builder()
        {
            this.entries = Maps.newHashMap();
        }

        private Builder( final AccessControlList acl )
        {
            this.entries = Maps.newHashMap();
            this.entries.putAll( acl.entries );
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
            return new AccessControlList( this );
        }
    }

}
