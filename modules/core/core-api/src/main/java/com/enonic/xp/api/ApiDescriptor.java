package com.enonic.xp.api;

import java.util.Objects;

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

    private final String displayName;

    private final String displayNameI18nKey;

    private final String description;

    private final String descriptionI18nKey;

    private final String documentationUrl;

    private final Boolean useInSlashApi;

    private ApiDescriptor( final Builder builder )
    {
        Preconditions.checkNotNull( builder.key, "key cannot be null" );

        this.key = builder.key;
        this.allowedPrincipals = builder.allowedPrincipals;
        this.displayName = builder.displayName;
        this.displayNameI18nKey = builder.displayNameI18nKey;
        this.description = builder.description;
        this.descriptionI18nKey = builder.descriptionI18nKey;
        this.documentationUrl = builder.documentationUrl;
        this.useInSlashApi = Objects.requireNonNullElse( builder.useInSlashApi, true );
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

    public String getDisplayNameI18nKey()
    {
        return displayNameI18nKey;
    }

    public String getDescription()
    {
        return description;
    }

    public String getDescriptionI18nKey()
    {
        return descriptionI18nKey;
    }

    public String getDocumentationUrl()
    {
        return documentationUrl;
    }

    public Boolean getUseInSlashApi()
    {
        return useInSlashApi;
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

        private String displayName;

        private String displayNameI18nKey;

        private String description;

        private String descriptionI18nKey;

        private String documentationUrl;

        private Boolean useInSlashApi;

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

        public Builder displayNameI18nKey( final String displayNameI18nKey )
        {
            this.displayNameI18nKey = displayNameI18nKey;
            return this;
        }

        public Builder description( final String description )
        {
            this.description = description;
            return this;
        }

        public Builder descriptionI18nKey( final String descriptionI18nKey )
        {
            this.descriptionI18nKey = descriptionI18nKey;
            return this;
        }

        public Builder documentationUrl( final String documentationUrl )
        {
            this.documentationUrl = documentationUrl;
            return this;
        }

        public Builder useInSlashApi( final Boolean useInSlashApi )
        {
            this.useInSlashApi = useInSlashApi;
            return this;
        }

        public ApiDescriptor build()
        {
            return new ApiDescriptor( this );
        }
    }
}
