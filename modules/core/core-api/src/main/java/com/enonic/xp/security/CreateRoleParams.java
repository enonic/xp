package com.enonic.xp.security;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

import static com.google.common.base.Preconditions.checkNotNull;

@PublicApi
public final class CreateRoleParams
{
    private final PrincipalKey key;

    private final String displayName;

    private final String description;

    private CreateRoleParams( final Builder builder )
    {
        this.key = checkNotNull( builder.principalKey, "roleKey is required for a role" );
        this.displayName = checkNotNull( builder.displayName, "displayName is required for a role" );
        this.description = builder.description;
    }

    public PrincipalKey getKey()
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

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private PrincipalKey principalKey;

        private String displayName;

        private String description;

        private Builder()
        {
        }

        public Builder roleKey( final PrincipalKey value )
        {
            Preconditions.checkArgument( value.isRole(), "Invalid PrincipalType for role key: " + value.getType() );
            this.principalKey = value;
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

        public CreateRoleParams build()
        {
            return new CreateRoleParams( this );
        }
    }
}
