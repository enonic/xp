package com.enonic.xp.admin.app;

import java.util.LinkedList;
import java.util.List;

import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

public class AdminApplicationDescriptor
{
    private final DescriptorKey key;

    private final String name;

    private final String shortName;

    private final String iconUrl;

    private final PrincipalKeys allowedPrincipals;

    private AdminApplicationDescriptor( final Builder builder )
    {
        key = builder.key;
        name = builder.name;
        shortName = builder.shortName;
        iconUrl = builder.iconUrl;
        allowedPrincipals = PrincipalKeys.from( builder.allowedPrincipals );
    }

    public DescriptorKey getKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public String getShortName()
    {
        return shortName;
    }

    public String getIconUrl()
    {
        return iconUrl;
    }

    public PrincipalKeys getAllowedPrincipals()
    {
        return allowedPrincipals;
    }

    public boolean isAccessAllowed( final PrincipalKeys principalKeys )
    {
        return principalKeys.stream().anyMatch( allowedPrincipals::contains );
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private DescriptorKey key;

        private String name;

        private String shortName;

        private String iconUrl;

        private List<PrincipalKey> allowedPrincipals = new LinkedList<>();

        private Builder()
        {
        }

        public Builder key( final DescriptorKey key )
        {
            this.key = key;
            return this;
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder shortName( final String shortName )
        {
            this.shortName = shortName;
            return this;
        }

        public Builder iconUrl( final String iconUrl )
        {
            this.iconUrl = iconUrl;
            return this;
        }

        public Builder addAllowedPrincipal( final PrincipalKey allowedPrincipal )
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