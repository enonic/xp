package com.enonic.wem.api.security;

import static com.google.common.base.Preconditions.checkArgument;

public final class Role
    extends Principal
{
    private Role( final Builder builder )
    {
        super( builder.principalKey, builder.displayName );
        checkArgument( builder.principalKey.isRole(), "Invalid Principal Type for Role: " + builder.principalKey.getType() );
    }

    public static Builder newRole()
    {
        return new Builder();
    }

    public static Builder newRole( final Role role )
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
            this.principalKey = value;
            return this;
        }

        public Builder displayName( final String value )
        {
            this.displayName = value;
            return this;
        }

        public Role build()
        {
            return new Role( this );
        }
    }
}
