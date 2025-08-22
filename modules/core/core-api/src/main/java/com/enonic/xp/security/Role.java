package com.enonic.xp.security;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class Role
    extends Principal
{
    private Role( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final Role role )
    {
        return new Builder( role );
    }

    public static final class Builder
        extends Principal.Builder<Builder>
    {
        private Builder()
        {
            super();
        }

        private Builder( final Role role )
        {
            super( role );
        }

        @Override
        protected void validate()
        {
            super.validate();
            Preconditions.checkArgument( this.key.isRole(), "Invalid Principal Type for Role: %s", this.key.getType() );
        }

        public Role build()
        {
            validate();
            return new Role( this );
        }
    }
}
