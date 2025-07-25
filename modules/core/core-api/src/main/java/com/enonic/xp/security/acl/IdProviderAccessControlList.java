package com.enonic.xp.security.acl;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

@PublicApi
public final class IdProviderAccessControlList
    implements Iterable<IdProviderAccessControlEntry>
{
    private static final IdProviderAccessControlList EMPTY = IdProviderAccessControlList.create().build();

    private final ImmutableMap<PrincipalKey, IdProviderAccessControlEntry> entries;

    private IdProviderAccessControlList( final Builder builder )
    {
        this.entries = ImmutableMap.copyOf( builder.entries );
    }

    public static IdProviderAccessControlList empty()
    {
        return EMPTY;
    }

    public static IdProviderAccessControlList of( final IdProviderAccessControlEntry... entries )
    {
        return IdProviderAccessControlList.create().addAll( entries ).build();
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

    public static Builder create( final IdProviderAccessControlList acl )
    {
        return new Builder( acl );
    }

    public PrincipalKeys getAllPrincipals()
    {
        return PrincipalKeys.from( this.entries.keySet() );
    }

    @Override
    public int hashCode()
    {
        return this.entries.hashCode();
    }

    public IdProviderAccessControlEntry getEntry( final PrincipalKey principalKey )
    {
        return this.entries.get( principalKey );
    }

    @Override
    public Iterator<IdProviderAccessControlEntry> iterator()
    {
        return entries.values().iterator();
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof IdProviderAccessControlList ) )
        {
            return false;
        }

        final IdProviderAccessControlList that = (IdProviderAccessControlList) o;
        return this.entries.equals( that.entries );
    }

    public static final class Builder
    {
        private final Map<PrincipalKey, IdProviderAccessControlEntry> entries;

        private Builder()
        {
            this.entries = new LinkedHashMap<>();
        }

        private Builder( final IdProviderAccessControlList acl )
        {
            this.entries = new LinkedHashMap<>( acl.entries );
        }

        public Builder add( final IdProviderAccessControlEntry entry )
        {
            this.entries.put( entry.getPrincipal(), entry );
            return this;
        }

        public Builder addAll( final Iterable<IdProviderAccessControlEntry> entries )
        {
            for ( IdProviderAccessControlEntry entry : entries )
            {
                this.entries.put( entry.getPrincipal(), entry );
            }
            return this;
        }

        public Builder addAll( final IdProviderAccessControlEntry... entries )
        {
            for ( IdProviderAccessControlEntry entry : entries )
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

        public IdProviderAccessControlList build()
        {
            return new IdProviderAccessControlList( this );
        }
    }

}
