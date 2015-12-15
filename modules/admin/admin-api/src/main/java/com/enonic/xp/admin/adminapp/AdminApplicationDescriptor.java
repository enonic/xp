package com.enonic.xp.admin.adminapp;

import java.util.LinkedList;
import java.util.List;

import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

public class AdminApplicationDescriptor
{
    private final DescriptorKey key;

    private final String displayName;

    private final String icon;

    private final PrincipalKeys allowedPrincipals;

    private AdminApplicationDescriptor( final Builder builder )
    {
        key = builder.key;
        displayName = builder.displayName;
        icon = builder.icon;
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

    public String getIcon()
    {
        return icon;
    }

    public PrincipalKeys getAllowedPrincipals()
    {
        return allowedPrincipals;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private DescriptorKey key;

        private String displayName;

        private String icon;

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

        public Builder icon( final String icon )
        {
            this.icon = icon;
            return this;
        }

        public Builder addAllowedPrincipals( final PrincipalKey allowedPrincipal )
        {
            this.allowedPrincipals.add( allowedPrincipal );
            return this;
        }

        public AdminApplicationDescriptor build()
        {
            return new AdminApplicationDescriptor( this );
        }
    }
}
