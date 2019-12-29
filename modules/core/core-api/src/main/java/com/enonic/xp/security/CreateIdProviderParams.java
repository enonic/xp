package com.enonic.xp.security;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.acl.IdProviderAccessControlList;

import static com.google.common.base.Preconditions.checkNotNull;

@PublicApi
public final class CreateIdProviderParams
{

    private final IdProviderKey idProviderKey;

    private final String displayName;

    private final String description;

    private final IdProviderConfig idProviderConfig;

    private final IdProviderAccessControlList idProviderPermissions;

    private CreateIdProviderParams( final Builder builder )
    {
        this.idProviderKey = checkNotNull( builder.idProviderKey, "idProviderKey is required" );
        this.displayName = checkNotNull( builder.displayName, "displayName is required" );
        this.description = builder.description;
        this.idProviderConfig = builder.idProviderConfig;
        this.idProviderPermissions =
            builder.idProviderPermissions == null ? IdProviderAccessControlList.empty() : builder.idProviderPermissions;
    }

    public IdProviderKey getKey()
    {
        return idProviderKey;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getDescription()
    {
        return description;
    }

    public IdProviderConfig getIdProviderConfig()
    {
        return idProviderConfig;
    }

    public IdProviderAccessControlList getIdProviderPermissions()
    {
        return idProviderPermissions;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private IdProviderKey idProviderKey;

        private String displayName;

        private String description;

        private IdProviderConfig idProviderConfig;

        private IdProviderAccessControlList idProviderPermissions;

        private Builder()
        {
        }

        public Builder key( final IdProviderKey value )
        {
            this.idProviderKey = value;
            return this;
        }

        public Builder displayName( final String value )
        {
            this.displayName = value;
            return this;
        }

        public Builder description( final String value )
        {
            this.description = value;
            return this;
        }

        public Builder idProviderConfig( final IdProviderConfig value )
        {
            this.idProviderConfig = value;
            return this;
        }

        public Builder permissions( final IdProviderAccessControlList permissions )
        {
            this.idProviderPermissions = permissions;
            return this;
        }

        public CreateIdProviderParams build()
        {
            return new CreateIdProviderParams( this );
        }
    }
}
