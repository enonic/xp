package com.enonic.xp.security;

import com.enonic.xp.annotation.PublicApi;

import static com.google.common.base.Preconditions.checkArgument;

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

    public static class Builder
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
            checkArgument( this.key.isRole(), "Invalid Principal Type for Role: " + this.key.getType() );
        }

        public Role build()
        {
            validate();
            return new Role( this );
        }
    }
}
