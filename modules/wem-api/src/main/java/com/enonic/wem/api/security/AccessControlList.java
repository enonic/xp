package com.enonic.wem.api.security;

import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public final class AccessControlList
    implements Iterable<AccessControlEntry>
{
    private final static AccessControlList EMPTY = AccessControlList.newACL().build();

    private final ImmutableMap<PrincipalKey, AccessControlEntry> entries;

    private AccessControlList( final Builder builder )
    {
        this.entries = ImmutableMap.copyOf( builder.entries );
    }

    public AccessControlList getEffective( AccessControlList parentAcl )
    {
        final AccessControlList.Builder effective = AccessControlList.newACL();
        // apply parent entries
        for ( AccessControlEntry parentEntry : parentAcl )
        {
            final PrincipalKey principal = parentEntry.getPrincipal();
            if ( this.entries.containsKey( principal ) )
            {
                final AccessControlEntry childEntry = this.entries.get( principal );
                final AccessControlEntry effectiveEntry = inheritFromParent( childEntry, parentEntry );
                effective.add( effectiveEntry );
            }
            else
            {
                effective.add( getEffectiveEntry( parentEntry ) );
            }
        }

        // apply child entries not in parent
        for ( AccessControlEntry childEntry : this )
        {
            if ( !parentAcl.entries.containsKey( childEntry.getPrincipal() ) )
            {
                effective.add( getEffectiveEntry( childEntry ) );
            }
        }

        return effective.build();
    }

    private AccessControlEntry inheritFromParent( final AccessControlEntry childEntry, final AccessControlEntry parentEntry )
    {
        final AccessControlEntry.Builder entry = AccessControlEntry.newACE().principal( childEntry.getPrincipal() );
        for ( Permission permission : Permission.values() )
        {
            if ( parentEntry.isSet( permission ) && !childEntry.isSet( permission ) )
            {
                // inherit permission from parent
                if ( parentEntry.isAllowed( permission ) )
                {
                    entry.allow( permission );
                }
                else
                {
                    entry.deny( permission );
                }
            }
            else
            {
                // set effective permission from child
                if ( childEntry.isAllowed( permission ) )
                {
                    entry.allow( permission );
                }
                else
                {
                    entry.deny( permission );
                }
            }
        }
        return entry.build();
    }

    private AccessControlEntry getEffectiveEntry( final AccessControlEntry entry )
    {
        final AccessControlEntry.Builder effective = AccessControlEntry.newACE().principal( entry.getPrincipal() );
        for ( Permission permission : Permission.values() )
        {
            if ( entry.isAllowed( permission ) )
            {
                effective.allow( permission );
            }
            else
            {
                effective.deny( permission );
            }
        }
        return effective.build();
    }

    public boolean isAllowedFor( final Permission permission, final PrincipalKey principal )
    {
        final AccessControlEntry entry = this.entries.get( principal );
        return entry != null && entry.isAllowed( permission );
    }

    public boolean isDeniedFor( final Permission permission, final PrincipalKey principal )
    {
        return !isAllowedFor( permission, principal );
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

    public static AccessControlList empty()
    {
        return EMPTY;
    }

    public static AccessControlList of( final AccessControlEntry... entries )
    {
        return AccessControlList.newACL().addAll( entries ).build();
    }

    public static Builder newACL()
    {
        return new Builder();
    }

    public static Builder newACL( final AccessControlList acl )
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
