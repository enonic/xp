package com.enonic.wem.api.security.acl;

import java.util.EnumSet;
import java.util.Objects;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.security.PrincipalKey;

import static com.google.common.base.Preconditions.checkNotNull;

public final class AccessControlEntry
{
    private final PrincipalKey principal;

    private final boolean inherited;

    private final ImmutableSet<Permission> allowedPermissions;

    private final ImmutableSet<Permission> deniedPermissions;

    private AccessControlEntry( final Builder builder )
    {
        this.principal = checkNotNull( builder.principal, "ACE principal cannot be null" );
        this.inherited = builder.inherited;
        this.allowedPermissions = Sets.immutableEnumSet( builder.allowedPermissions );
        this.deniedPermissions = Sets.immutableEnumSet( builder.deniedPermissions );
    }

    public PrincipalKey getPrincipal()
    {
        return principal;
    }

    public boolean isInherited()
    {
        return this.inherited;
    }

    public Iterable<Permission> getAllowedPermissions()
    {
        return allowedPermissions;
    }

    public Iterable<Permission> getDeniedPermissions()
    {
        return deniedPermissions;
    }

    public boolean isAllowed( final Permission permission )
    {
        return this.allowedPermissions.contains( permission ) && !this.deniedPermissions.contains( permission );
    }

    public boolean isDenied( final Permission permission )
    {
        return !this.isAllowed( permission );
    }

    public boolean isSet( final Permission permission )
    {
        return this.allowedPermissions.contains( permission ) || this.deniedPermissions.contains( permission );
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder( principal.toString() );
        sb.append( "[" );
        boolean empty = true;
        for ( Permission permission : Permission.values() )
        {
            if ( allowedPermissions.contains( permission ) )
            {
                if ( !empty )
                {
                    sb.append( ", " );
                }
                sb.append( "+" ).append( permission.toString().toLowerCase() );
                empty = false;
            }
            else if ( deniedPermissions.contains( permission ) )
            {
                if ( !empty )
                {
                    sb.append( ", " );
                }
                sb.append( "-" ).append( permission.toString().toLowerCase() );
                empty = false;
            }
        }
        sb.append( "]" );
        return sb.toString();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof AccessControlEntry ) )
        {
            return false;
        }
        final AccessControlEntry that = (AccessControlEntry) o;

        if ( !principal.equals( that.principal ) )
        {
            return false;
        }
        if ( !allowedPermissions.equals( that.allowedPermissions ) )
        {
            return false;
        }
        if ( !deniedPermissions.equals( that.deniedPermissions ) )
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( principal, allowedPermissions, deniedPermissions );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder newACE( final AccessControlEntry ace )
    {
        return new Builder( ace );
    }

    public static class Builder
    {
        private PrincipalKey principal;

        private boolean inherited;

        private final EnumSet<Permission> allowedPermissions;

        private final EnumSet<Permission> deniedPermissions;

        private Builder()
        {
            this.allowedPermissions = EnumSet.noneOf( Permission.class );
            this.deniedPermissions = EnumSet.noneOf( Permission.class );
        }

        private Builder( final AccessControlEntry ace )
        {
            this.principal = ace.principal;
            this.inherited = ace.inherited;
            this.allowedPermissions = EnumSet.copyOf( ace.allowedPermissions );
            this.deniedPermissions = EnumSet.copyOf( ace.deniedPermissions );
        }

        public Builder principal( final PrincipalKey principal )
        {
            this.principal = principal;
            return this;
        }

        public Builder allow( final Permission permission )
        {
            this.deniedPermissions.remove( permission );
            this.allowedPermissions.add( permission );
            return this;
        }

        public Builder deny( final Permission permission )
        {
            this.allowedPermissions.remove( permission );
            this.deniedPermissions.add( permission );
            return this;
        }

        public Builder remove( final Permission permission )
        {
            this.allowedPermissions.remove( permission );
            this.deniedPermissions.remove( permission );
            return this;
        }

        public Builder inherited( final boolean inherited )
        {
            this.inherited = inherited;
            return this;
        }

        public Builder allowAll()
        {
            this.allow( Permission.CREATE );
            this.allow( Permission.DELETE );
            this.allow( Permission.PUBLISH );
            this.allow( Permission.READ );
            this.allow( Permission.MODIFY );
            this.allow( Permission.READ_PERMISSIONS );
            this.allow( Permission.WRITE_PERMISSIONS );

            return this;
        }

        public AccessControlEntry build()
        {
            return new AccessControlEntry( this );
        }
    }

}
