package com.enonic.xp.admin.tool;

import java.util.Objects;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;

@PublicApi
public final class AdminToolDescriptor
{
    private final DescriptorKey key;

    private final String displayName;

    private final String displayNameI18nKey;

    private final String description;

    private final String descriptionI18nKey;

    private final PrincipalKeys allowedPrincipals;

    private final DescriptorKeys apiMounts;

    private final ImmutableSet<String> interfaces;

    private AdminToolDescriptor( final Builder builder )
    {
        key = builder.key;
        displayName = builder.displayName;
        displayNameI18nKey = builder.displayNameI18nKey;
        description = builder.description;
        descriptionI18nKey = builder.descriptionI18nKey;
        allowedPrincipals = PrincipalKeys.from( builder.allowedPrincipals.build() );
        apiMounts = Objects.requireNonNullElse( builder.apiMounts, DescriptorKeys.empty() );
        interfaces = builder.interfaces.build();
    }

    public DescriptorKey getKey()
    {
        return key;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getName()
    {
        return this.key.getName();
    }

    public ApplicationKey getApplicationKey()
    {
        return this.key.getApplicationKey();
    }

    public String getDescription()
    {
        return description;
    }

    public String getDisplayNameI18nKey()
    {
        return displayNameI18nKey;
    }

    public String getDescriptionI18nKey()
    {
        return descriptionI18nKey;
    }

    public PrincipalKeys getAllowedPrincipals()
    {
        return allowedPrincipals;
    }

    public boolean isAccessAllowed( final PrincipalKeys principalKeys )
    {
        return principalKeys.contains( RoleKeys.ADMIN ) || principalKeys.stream().anyMatch( allowedPrincipals::contains );
    }

    public Set<String> getInterfaces()
    {
        return interfaces;
    }

    public boolean hasInterface( final String interfaceName )
    {
        return interfaces.contains( interfaceName );
    }

    public DescriptorKeys getApiMounts()
    {
        return apiMounts;
    }

    public static ResourceKey toResourceKey( final DescriptorKey key )
    {
        return ResourceKey.from( key.getApplicationKey(), "admin/tools/" + key.getName() + "/" + key.getName() + ".xml" );
    }

    public static ResourceKey toIconResourceKey( final DescriptorKey key )
    {
        return ResourceKey.from( key.getApplicationKey(), "admin/tools/" + key.getName() + "/" + key.getName() + ".svg" );
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private DescriptorKey key;

        private String displayName;

        private String displayNameI18nKey;

        private String description;

        private String descriptionI18nKey;

        private DescriptorKeys apiMounts;

        private final ImmutableSet.Builder<PrincipalKey> allowedPrincipals = ImmutableSet.builder();

        private final ImmutableSet.Builder<String> interfaces = ImmutableSet.builder();

        private Builder()
        {
        }

        public Builder key( final DescriptorKey key )
        {
            this.key = key;
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

        public Builder addAllowedPrincipals( final PrincipalKeys allowedPrincipals )
        {
            this.allowedPrincipals.addAll( allowedPrincipals );
            return this;
        }

        public Builder addAllowedPrincipals( final PrincipalKey allowedPrincipal )
        {
            this.allowedPrincipals.add( allowedPrincipal );
            return this;
        }

        public Builder apiMounts( final DescriptorKeys apiMounts )
        {
            this.apiMounts = apiMounts;
            return this;
        }

        public Builder addInterface( final String interfaceName )
        {
            this.interfaces.add( interfaceName );
            return this;
        }

        public Builder addInterfaces( final Iterable<String> interfaceNames )
        {
            this.interfaces.addAll( interfaceNames );
            return this;
        }

        public AdminToolDescriptor build()
        {
            return new AdminToolDescriptor( this );
        }
    }
}
