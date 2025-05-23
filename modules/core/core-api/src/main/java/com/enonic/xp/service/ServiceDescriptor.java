package com.enonic.xp.service;

import java.util.Collection;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;

public final class ServiceDescriptor
{
    private static final String ROOT_SERVICE_PREFIX = "services/";

    private final DescriptorKey key;

    private final PrincipalKeys allowedPrincipals;

    private ServiceDescriptor( final Builder builder )
    {
        key = builder.key;
        allowedPrincipals = builder.allowedPrincipals == null ? null : PrincipalKeys.from( builder.allowedPrincipals );
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

    public static ResourceKey toRootResourceKey( final DescriptorKey key )
    {
        return ResourceKey.from( key.getApplicationKey(), ROOT_SERVICE_PREFIX + key.getName() + "/" + key.getName() + ".xml" );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private DescriptorKey key;

        private Collection<PrincipalKey> allowedPrincipals;

        private Builder()
        {
        }

        public Builder key( final DescriptorKey key )
        {
            this.key = key;
            return this;
        }

        public Builder setAllowedPrincipals( final Collection<PrincipalKey> allowedPrincipals )
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
