package com.enonic.xp.api;

import java.util.Objects;
import java.util.Set;

import com.google.common.base.Preconditions;

import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;

public final class ApiDescriptor
{
    private final DescriptorKey key;

    private final PrincipalKeys allowedPrincipals;

    private final Set<ApiMount> mounts;

    private final ApiContextPath contextPath;

    private ApiDescriptor( final Builder builder )
    {
        Preconditions.checkNotNull( builder.key, "key cannot be null" );
        Preconditions.checkNotNull( builder.mounts, "mounts cannot be null" );

        this.key = builder.key;
        this.allowedPrincipals = builder.allowedPrincipals;
        this.mounts = Set.copyOf( builder.mounts );
        this.contextPath = Objects.requireNonNullElse( builder.contextPath, ApiContextPath.DEFAULT );
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

    public Set<ApiMount> getMounts()
    {
        return mounts;
    }

    public ApiContextPath getContextPath()
    {
        return contextPath;
    }

    public boolean hasMount( final ApiMount mount )
    {
        return mounts.contains( mount );
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

        private Set<ApiMount> mounts;

        private ApiContextPath contextPath;

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

        public Builder mounts( final Set<ApiMount> mounts )
        {
            this.mounts = mounts;
            return this;
        }

        public Builder contextPath( final ApiContextPath contextPath )
        {
            this.contextPath = contextPath;
            return this;
        }

        public ApiDescriptor build()
        {
            return new ApiDescriptor( this );
        }
    }
}
