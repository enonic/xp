package com.enonic.xp.api;

import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.util.GenericValue;

import static java.util.Objects.requireNonNull;


public final class ApiDescriptor
{
    private final DescriptorKey key;

    private final PrincipalKeys allowedPrincipals;

    private final String title;

    private final String titleI18nKey;

    private final String description;

    private final String descriptionI18nKey;

    private final String documentationUrl;

    private final ImmutableSet<String> mount;

    private final GenericValue schemaConfig;

    private ApiDescriptor( final Builder builder )
    {
        requireNonNull( builder.key, "key cannot be null" );
        requireNonNull( builder.allowedPrincipals, "allowedPrincipals cannot be null" );
        Preconditions.checkArgument( !builder.allowedPrincipals.isEmpty(), "allowedPrincipals cannot be empty" );

        this.key = builder.key;
        this.allowedPrincipals = builder.allowedPrincipals;
        this.title = builder.title;
        this.titleI18nKey = builder.titleI18nKey;
        this.description = builder.description;
        this.descriptionI18nKey = builder.descriptionI18nKey;
        this.documentationUrl = builder.documentationUrl;
        this.mount = builder.mount;
        this.schemaConfig = builder.schemaConfig.build();
    }

    public DescriptorKey getKey()
    {
        return key;
    }

    public PrincipalKeys getAllowedPrincipals()
    {
        return allowedPrincipals;
    }

    public String getTitle()
    {
        return title;
    }

    public String getDescription()
    {
        return description;
    }

    public String getDescriptionI18nKey()
    {
        return descriptionI18nKey;
    }

    public String getTitleI18nKey()
    {
        return titleI18nKey;
    }

    public String getDocumentationUrl()
    {
        return documentationUrl;
    }

    public Set<String> getMount()
    {
        return mount;
    }

    public GenericValue getSchemaConfig()
    {
        return schemaConfig;
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

        private String title;

        private String titleI18nKey;

        private String description;

        private String descriptionI18nKey;

        private String documentationUrl;

        private ImmutableSet<String> mount = ImmutableSet.of();

        private final GenericValue.ObjectBuilder schemaConfig = GenericValue.newObject();

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

        public Builder title( final String title )
        {
            this.title = title;
            return this;
        }

        public Builder titleI18nKey( final String titleI18nKey )
        {
            this.titleI18nKey = titleI18nKey;
            return this;
        }

        public Builder title( final LocalizedText text )
        {
            this.title = text.text();
            this.titleI18nKey = text.i18n();
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

        public Builder schemaConfig( final GenericValue value )
        {
            value.properties().forEach( e -> this.schemaConfig.put( e.getKey(), e.getValue() ) );
            return this;
        }

        public ApiDescriptor build()
        {
            return new ApiDescriptor( this );
        }
    }
}
