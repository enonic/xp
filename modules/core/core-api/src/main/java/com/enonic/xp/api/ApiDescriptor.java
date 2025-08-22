package com.enonic.xp.api;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;

@PublicApi
public final class ApiDescriptor
{
    private final DescriptorKey key;

    private final PrincipalKeys allowedPrincipals;

    private final String displayName;

    private final String description;

    private final String documentationUrl;

    private final boolean mount;

    private ApiDescriptor( final Builder builder )
    {
        Objects.requireNonNull( builder.key, "key cannot be null" );
        Objects.requireNonNull( builder.allowedPrincipals, "allowedPrincipals cannot be null" );
        Preconditions.checkArgument( !builder.allowedPrincipals.isEmpty(), "allowedPrincipals cannot be empty" );

        this.key = builder.key;
        this.allowedPrincipals = builder.allowedPrincipals;
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.documentationUrl = builder.documentationUrl;
        this.mount = Objects.requireNonNullElse( builder.mount, false );
    }

    public DescriptorKey getKey()
    {
        return key;
    }

    public PrincipalKeys getAllowedPrincipals()
    {
        return allowedPrincipals;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getDescription()
    {
        return description;
    }

    public String getDocumentationUrl()
    {
        return documentationUrl;
    }

    public boolean isMount()
    {
        return mount;
    }

    public boolean isAccessAllowed( final PrincipalKeys principalKeys )
    {
        return principalKeys.contains( RoleKeys.ADMIN ) || allowedPrincipals.stream().anyMatch( principalKeys::contains );
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

        private String displayName;

        private String description;

        private String documentationUrl;

        private Boolean mount;

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

        public Builder displayName( final String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public Builder description( final String description )
        {
            this.description = description;
            return this;
        }

        public Builder documentationUrl( final String documentationUrl )
        {
            this.documentationUrl = documentationUrl;
            return this;
        }

        public Builder mount( final Boolean mount )
        {
            this.mount = mount;
            return this;
        }

        public ApiDescriptor build()
        {
            return new ApiDescriptor( this );
        }
    }
}
