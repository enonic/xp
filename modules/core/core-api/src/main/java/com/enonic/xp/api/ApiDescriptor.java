package com.enonic.xp.api;

import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;

public final class ApiDescriptor
{
    private final DescriptorKey key;

    private final PrincipalKeys allowedPrincipals;

    private ApiDescriptor( final Builder builder )
    {
        this.key = builder.key;
        this.allowedPrincipals = builder.allowedPrincipals;
    }

    public DescriptorKey key()
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

    public ResourceKey toResourceKey( final String extension )
    {
        return toResourceKey( this.key, extension );
    }

    public static ResourceKey toResourceKey( final DescriptorKey key, final String extension )
    {
        if ( "api".equals( key.getName() ) )
        {
            return ResourceKey.from( key.getApplicationKey(), "apis/api." + extension );
        }
        else
        {
            return ResourceKey.from( key.getApplicationKey(), "apis/" + key.getName() + "/" + key.getName() + "." + extension );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
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
