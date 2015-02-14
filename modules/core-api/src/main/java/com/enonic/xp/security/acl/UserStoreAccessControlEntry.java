package com.enonic.xp.security.acl;

import java.util.Objects;

import com.enonic.xp.security.PrincipalKey;

import static com.google.common.base.Preconditions.checkNotNull;

public final class UserStoreAccessControlEntry
{
    private final PrincipalKey principal;

    private final UserStoreAccess access;

    private UserStoreAccessControlEntry( final Builder builder )
    {
        this.principal = checkNotNull( builder.principal, "principal cannot be null" );
        this.access = checkNotNull( builder.access, "access cannot be null" );
    }

    public PrincipalKey getPrincipal()
    {
        return principal;
    }

    public UserStoreAccess getAccess()
    {
        return access;
    }

    public boolean hasAtLeastAccess( final UserStoreAccess access )
    {
        return this.access.compareTo( access ) >= 0;
    }

    @Override
    public String toString()
    {
        return principal.toString() + "[" + access.toString().toLowerCase() + "]";
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof UserStoreAccessControlEntry ) )
        {
            return false;
        }
        final UserStoreAccessControlEntry that = (UserStoreAccessControlEntry) o;
        return this.principal.equals( that.principal ) && this.access == that.access;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( principal, access );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final UserStoreAccessControlEntry ace )
    {
        return new Builder( ace );
    }

    public static class Builder
    {
        private PrincipalKey principal;

        private UserStoreAccess access;

        private Builder()
        {
        }

        private Builder( final UserStoreAccessControlEntry ace )
        {
            this.principal = ace.principal;
            this.access = ace.access;
        }

        public Builder principal( final PrincipalKey principal )
        {
            this.principal = principal;
            return this;
        }

        public Builder access( final UserStoreAccess access )
        {
            this.access = access;
            return this;
        }

        public UserStoreAccessControlEntry build()
        {
            return new UserStoreAccessControlEntry( this );
        }
    }

}
