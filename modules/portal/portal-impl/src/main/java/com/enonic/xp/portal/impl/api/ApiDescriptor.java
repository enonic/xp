package com.enonic.xp.portal.impl.api;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;

public final class ApiDescriptor
{
    private static final String API_DESCRIPTOR_PATH = "api/api.xml";

    private final ApplicationKey applicationKey;

    private final PrincipalKeys allowedPrincipals;

    private ApiDescriptor( final Builder builder )
    {
        this.applicationKey = builder.applicationKey;
        this.allowedPrincipals = builder.allowedPrincipals;
    }

    public ApplicationKey getApplicationKey()
    {
        return applicationKey;
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

    public static ResourceKey toResourceKey( final ApplicationKey applicationKey )
    {
        return ResourceKey.from( applicationKey, API_DESCRIPTOR_PATH );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {

        private ApplicationKey applicationKey;

        private PrincipalKeys allowedPrincipals;

        private Builder()
        {
        }

        public Builder applicationKey( final ApplicationKey applicationKey )
        {
            this.applicationKey = applicationKey;
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
