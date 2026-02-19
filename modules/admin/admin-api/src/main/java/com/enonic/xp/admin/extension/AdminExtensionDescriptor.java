package com.enonic.xp.admin.extension;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.descriptor.Descriptor;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;

@PublicApi
public final class AdminExtensionDescriptor
    extends Descriptor
{
    private final String displayName;

    private final String displayNameI18nKey;

    private final String description;

    private final String descriptionI18nKey;

    private final Icon icon;

    private final ImmutableSet<String> interfaces;

    private final PrincipalKeys allowedPrincipals;

    private final ImmutableMap<String, String> config;

    private AdminExtensionDescriptor( final Builder builder )
    {
        super( builder.key );
        this.displayName = builder.displayName;
        this.displayNameI18nKey = builder.displayNameI18nKey;
        this.description = builder.description;
        this.descriptionI18nKey = builder.descriptionI18nKey;
        this.icon = builder.icon;
        this.interfaces = builder.interfaces;
        this.allowedPrincipals = builder.allowedPrincipals;
        this.config = ImmutableMap.copyOf( builder.config );
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

    public Icon getIcon()
    {
        return icon;
    }

    public Set<String> getInterfaces()
    {
        return interfaces;
    }

    public boolean hasInterface( final String interfaceName )
    {
        return interfaces.contains( interfaceName );
    }

    public PrincipalKeys getAllowedPrincipals()
    {
        return allowedPrincipals;
    }

    public boolean isAccessAllowed( final PrincipalKeys principalKeys )
    {
        return allowedPrincipals == null || principalKeys.contains( RoleKeys.ADMIN ) ||
            principalKeys.stream().anyMatch( allowedPrincipals::contains );
    }

    public Map<String, String> getConfig()
    {
        return config;
    }

    public static AdminExtensionDescriptor.Builder create()
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

        private Icon icon;

        private ImmutableSet<String> interfaces = ImmutableSet.of();

        private PrincipalKeys allowedPrincipals;

        public final Map<String, String> config = new HashMap<>();

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

        public Builder displayName( final LocalizedText text )
        {
            this.displayName = text.text();
            this.displayNameI18nKey = text.i18n();
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

        public Builder description( final LocalizedText text )
        {
            this.description = text.text();
            this.descriptionI18nKey = text.i18n();
            return this;
        }

        public Builder setIcon( final Icon icon )
        {
            this.icon = icon;
            return this;
        }

        public Builder interfaces( final String... interfaceNames )
        {
            this.interfaces = ImmutableSet.copyOf( interfaceNames );
            return this;
        }

        public Builder allowedPrincipals( final PrincipalKeys allowedPrincipals )
        {
            this.allowedPrincipals = allowedPrincipals;
            return this;
        }

        public Builder addProperty( final String key, final String value )
        {
            this.config.put( key, value );
            return this;
        }

        public AdminExtensionDescriptor build()
        {
            return new AdminExtensionDescriptor( this );
        }
    }
}
