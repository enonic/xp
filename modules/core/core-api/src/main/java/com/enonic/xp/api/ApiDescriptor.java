package com.enonic.xp.api;

import java.util.Objects;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;


public final class ApiDescriptor
{
    private final DescriptorKey key;

    private final PrincipalKeys allowedPrincipals;

    private final String displayName;

    private final String displayNameI18nKey;

    private final String description;

    private final String descriptionI18nKey;

    private final String documentationUrl;

    private final ImmutableSet<String> mount;

    private ApiDescriptor( final Builder builder )
    {
        Objects.requireNonNull( builder.key, "key cannot be null" );
        Objects.requireNonNull( builder.allowedPrincipals, "allowedPrincipals cannot be null" );
        Preconditions.checkArgument( !builder.allowedPrincipals.isEmpty(), "allowedPrincipals cannot be empty" );

        this.key = builder.key;
        this.allowedPrincipals = builder.allowedPrincipals;
        this.displayName = builder.displayName;
        this.displayNameI18nKey = builder.displayNameI18nKey;
        this.description = builder.description;
        this.descriptionI18nKey = builder.descriptionI18nKey;
        this.documentationUrl = builder.documentationUrl;
        this.mount = builder.mount;
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

    public String getDescriptionI18nKey()
    {
        return descriptionI18nKey;
    }

    public String getDisplayNameI18nKey()
    {
        return displayNameI18nKey;
    }

    public String getDocumentationUrl()
    {
        return documentationUrl;
    }

    public Set<String> getMount()
    {
        return mount;
    }

    public boolean isAccessAllowed( final PrincipalKeys principalKeys )
    {
        return principalKeys.contains( RoleKeys.ADMIN ) || allowedPrincipals.stream().anyMatch( principalKeys::contains );
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

        private ImmutableSet<String> mount = ImmutableSet.of();

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

        public Builder displayName( final LocalizedText text )
        {
            this.displayName = text.text();
            this.displayNameI18nKey = text.i18n();
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

        public Builder description( final LocalizedText text )
        {
            this.description = text.text();
            this.descriptionI18nKey = text.i18n();
            return this;
        }

        public Builder documentationUrl( final String documentationUrl )
        {
            this.documentationUrl = documentationUrl;
            return this;
        }

        public Builder mount( final String... mount )
        {
            this.mount = ImmutableSet.copyOf( mount );
            return this;
        }

        public ApiDescriptor build()
        {
            return new ApiDescriptor( this );
        }
    }
}
