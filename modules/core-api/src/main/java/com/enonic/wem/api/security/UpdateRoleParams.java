package com.enonic.wem.api.security;

import com.google.common.base.Preconditions;

import static com.google.common.base.Preconditions.checkNotNull;

public final class UpdateRoleParams
{
    private final PrincipalKey key;

    private final String displayName;

    private UpdateRoleParams( final Builder builder )
    {
        this.key = checkNotNull( builder.principalKey, "roleKey is required for a role" );
        this.displayName = builder.displayName;
    }

    public PrincipalKey getKey()
    {
        return key;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public Role update( final Role source )
    {
        Role.Builder result = Role.create( source );
        if ( this.displayName != null )
        {
            result.displayName( this.getDisplayName() );
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

        private Builder()
        {
        }

        private Builder( final Role role )
        {
            this.principalKey = role.getKey();
            this.displayName = role.getDisplayName();
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

        public UpdateRoleParams build()
        {
            return new UpdateRoleParams( this );
        }
    }
}
