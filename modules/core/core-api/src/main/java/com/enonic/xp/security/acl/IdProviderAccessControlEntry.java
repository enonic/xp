package com.enonic.xp.security.acl;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.PrincipalKey;

import static com.google.common.base.Preconditions.checkNotNull;

@PublicApi
public final class IdProviderAccessControlEntry
{
    private final PrincipalKey principal;

    private final IdProviderAccess access;

    private IdProviderAccessControlEntry( final Builder builder )
    {
        this.principal = checkNotNull( builder.principal, "principal cannot be null" );
        this.access = checkNotNull( builder.access, "access cannot be null" );
    }

    public PrincipalKey getPrincipal()
    {
        return principal;
    }

    public static Builder create( final IdProviderAccessControlEntry ace )
    {
        return new Builder( ace );
    }

    @Override
    public String toString()
    {
        return principal.toString() + "[" + access.toString().toLowerCase() + "]";
    }

    public IdProviderAccess getAccess()
    {
        return access;
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

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof IdProviderAccessControlEntry ) )
        {
            return false;
        }
        final IdProviderAccessControlEntry that = (IdProviderAccessControlEntry) o;
        return this.principal.equals( that.principal ) && this.access == that.access;
    }

    public static class Builder
    {
        private PrincipalKey principal;

        private IdProviderAccess access;

        private Builder()
        {
        }

        private Builder( final IdProviderAccessControlEntry ace )
        {
            this.principal = ace.principal;
            this.access = ace.access;
        }

        public Builder principal( final PrincipalKey principal )
        {
            this.principal = principal;
            return this;
        }

        public Builder access( final IdProviderAccess access )
        {
            this.access = access;
            return this;
        }

        public IdProviderAccessControlEntry build()
        {
            return new IdProviderAccessControlEntry( this );
        }
    }

}
