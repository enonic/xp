package com.enonic.xp.service;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;

public final class ServiceDescriptor
{
    private final DescriptorKey key;

    private final PrincipalKeys allowedPrincipals;

    private ServiceDescriptor( final Builder builder )
    {
        key = builder.key;
        allowedPrincipals = builder.allowedPrincipals;
    }

    public DescriptorKey getKey()
    {
        return key;
    }

    public PrincipalKeys getAllowedPrincipals()
    {
        return allowedPrincipals;
    }

    public boolean isAccessAllowed( final PrincipalKeys principalKeys )
    {
        return allowedPrincipals == null || principalKeys.contains( RoleKeys.ADMIN ) ||
            allowedPrincipals.stream().anyMatch( principalKeys::contains );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private DescriptorKey key;

        private PrincipalKeys allowedPrincipals;

        private Builder()
        {
        }

        public Builder key( final DescriptorKey key )
        {
            this.key = key;
            return this;
        }

        public Builder allowedPrincipals( final PrincipalKeys allowedPrincipals )
        {
            this.allowedPrincipals = allowedPrincipals;
            return this;
        }

        public ServiceDescriptor build()
        {
            return new ServiceDescriptor( this );
        }
    }
}
