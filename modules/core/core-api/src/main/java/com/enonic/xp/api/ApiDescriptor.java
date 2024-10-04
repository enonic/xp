package com.enonic.xp.api;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;

@PublicApi
public final class ApiDescriptor
{
    private final DescriptorKey key;

    private final PrincipalKeys allowedPrincipals;

    private ApiDescriptor( final Builder builder )
    {
        Preconditions.checkNotNull( builder.key, "key cannot be null" );

        this.key = builder.key;
        this.allowedPrincipals = builder.allowedPrincipals;
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

    public static ResourceKey toResourceKey( final DescriptorKey key, final String extension )
    {
        return ResourceKey.from( key.getApplicationKey(), "apis/" + key.getName() + "/" + key.getName() + "." + extension );
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

        public ApiDescriptor build()
        {
            return new ApiDescriptor( this );
        }
    }
}
