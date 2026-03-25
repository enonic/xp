package com.enonic.xp.admin.tool;

import java.util.Objects;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.descriptor.Descriptor;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;


public final class AdminToolDescriptor
    extends Descriptor
{
    private final String title;

    private final String titleI18nKey;

    private final String description;

    private final String descriptionI18nKey;

    private final PrincipalKeys allowedPrincipals;

    private final DescriptorKeys apiMounts;

    private final ImmutableSet<String> interfaces;

    private final Icon icon;

    private AdminToolDescriptor( final Builder builder )
    {
        super( builder.key );
        title = builder.title;
        titleI18nKey = builder.titleI18nKey;
        description = builder.description;
        descriptionI18nKey = builder.descriptionI18nKey;
        allowedPrincipals = PrincipalKeys.from( builder.allowedPrincipals.build() );
        apiMounts = Objects.requireNonNullElse( builder.apiMounts, DescriptorKeys.empty() );
        interfaces = builder.interfaces;
        icon = builder.icon;
    }

    public String getTitle()
    {
        return title;
    }

    public String getDescription()
    {
        return description;
    }

    public String getTitleI18nKey()
    {
        return titleI18nKey;
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

    public Icon getIcon()
    {
        return icon;
    }

    public DescriptorKeys getApiMounts()
    {
        return apiMounts;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private DescriptorKey key;

        private String title;

        private String titleI18nKey;

        private String description;

        private String descriptionI18nKey;

        private DescriptorKeys apiMounts;

        private final ImmutableSet.Builder<PrincipalKey> allowedPrincipals = ImmutableSet.builder();

        private ImmutableSet<String> interfaces = ImmutableSet.of();

        private Icon icon;

        private Builder()
        {
        }

        public Builder key( final DescriptorKey key )
        {
            this.key = key;
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

        public Builder interfaces( final String... interfaces )
        {
            this.interfaces = ImmutableSet.copyOf( interfaces );
            return this;
        }

        public Builder setIcon( final Icon icon )
        {
            this.icon = icon;
            return this;
        }

        public AdminToolDescriptor build()
        {
            return new AdminToolDescriptor( this );
        }
    }
}
