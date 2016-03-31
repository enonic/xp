package com.enonic.xp.security;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import static com.google.common.base.Preconditions.checkNotNull;

@Beta
public final class UpdateRoleParams
{
    private final PrincipalKey key;

    private final String displayName;

    private final String description;

    private UpdateRoleParams( final Builder builder )
    {
        this.key = checkNotNull( builder.principalKey, "roleKey is required for a role" );
        this.displayName = builder.displayName;
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

    public Role update( final Role source )
    {
        Role.Builder result = Role.create( source );
        if ( this.displayName != null )
        {
            result.displayName( this.getDisplayName() );
        }

        if ( this.description != null )
        {
            result.description( this.getDescription() );
        }
        return result.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final Role role )
    {
        return new Builder( role );
    }

    public static class Builder
    {
        private PrincipalKey principalKey;

        private String displayName;

        private String description;

        private Builder()
        {
        }

        private Builder( final Role role )
        {
            this.principalKey = role.getKey();
            this.displayName = role.getDisplayName();
            this.description = role.getDescription();
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

        public UpdateRoleParams build()
        {
            return new UpdateRoleParams( this );
        }
    }
}
