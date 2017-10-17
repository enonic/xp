package com.enonic.xp.admin.tool;

import java.util.LinkedList;
import java.util.List;

import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;

public class AdminToolDescriptor
{
    private final DescriptorKey key;

    private final String displayName;

    private final String description;

    private final PrincipalKeys allowedPrincipals;

    private AdminToolDescriptor( final Builder builder )
    {
        key = builder.key;
        displayName = builder.displayName;
        description = builder.description;
        allowedPrincipals = PrincipalKeys.from( builder.allowedPrincipals );
    }

    public DescriptorKey getKey()
    {
        return key;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getDescription()
    {
        return description;
    }

    public PrincipalKeys getAllowedPrincipals()
    {
        return allowedPrincipals;
    }

    public boolean isAccessAllowed( final PrincipalKeys principalKeys )
    {
        return principalKeys.contains( RoleKeys.ADMIN ) || principalKeys.stream().
            anyMatch( allowedPrincipals::contains );
    }

    public boolean isAppLauncherApplication()
    {
        return displayName != null;
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

        private String description;

        private List<PrincipalKey> allowedPrincipals = new LinkedList<>();

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

        public Builder addAllowedPrincipals( final PrincipalKey allowedPrincipal )
        {
            this.allowedPrincipals.add( allowedPrincipal );
            return this;
        }

        public AdminToolDescriptor build()
        {
            return new AdminToolDescriptor( this );
        }
    }
}
