package com.enonic.xp.admin.widget;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.descriptor.Descriptor;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;

@PublicApi
public class WidgetDescriptor
    extends Descriptor
{
    private final String displayName;

    private final String description;

    private final Icon icon;

    private final ImmutableSet<String> interfaces;

    private final PrincipalKeys allowedPrincipals;

    private final ImmutableMap<String, String> config;

    private static final String URL_PREFIX = "_/widgets/";

    private WidgetDescriptor( final Builder builder )
    {
        super( builder.key );
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.icon = builder.icon;
        this.interfaces = ImmutableSet.copyOf( builder.interfaces );
        this.allowedPrincipals = builder.allowedPrincipals == null ? null : PrincipalKeys.from( builder.allowedPrincipals );
        this.config = ImmutableMap.copyOf( builder.config );
    }

    public String getUrl()
    {
        return URL_PREFIX + getApplicationKey().toString() + "/" + getName();
    }

    public String getKeyString()
    {
        return getKey().toString();
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getDescription()
    {
        return description;
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
        return allowedPrincipals == null || principalKeys.contains( RoleKeys.ADMIN ) || principalKeys.stream().
            anyMatch( allowedPrincipals::contains );
    }

    public Map<String, String> getConfig()
    {
        return config;
    }

    public static WidgetDescriptor.Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private DescriptorKey key;

        private String displayName;

        private String description;

        private Icon icon;

        public final Set<String> interfaces = new TreeSet<>();

        private Collection<PrincipalKey> allowedPrincipals;

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

        public Builder description( final String description )
        {
            this.description = description;
            return this;
        }

        public Builder setIcon( final Icon icon )
        {
            this.icon = icon;
            return this;
        }

        public Builder addInterface( final String interfaceName )
        {
            this.interfaces.add( interfaceName );
            return this;
        }

        public Builder setAllowedPrincipals( final Collection<PrincipalKey> allowedPrincipals )
        {
            this.allowedPrincipals = allowedPrincipals;
            return this;
        }

        public Builder addProperty( final String key, final String value )
        {
            this.config.put( key, value );
            return this;
        }

        public WidgetDescriptor build()
        {
            return new WidgetDescriptor( this );
        }
    }
}
