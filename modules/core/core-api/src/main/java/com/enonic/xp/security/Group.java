package com.enonic.xp.security;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class Group
    extends Principal
{
    private Group( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final Group group )
    {
        return new Builder( group );
    }

    public static final class Builder
        extends Principal.Builder<Builder>
    {
        private Builder()
        {
            super();
        }

        private Builder( final Group group )
        {
            super( group );
        }


        @Override
        protected void validate()
        {
            super.validate();
            Preconditions.checkArgument( this.key.isGroup(), "Invalid Principal Type for Group: %s", this.key.getType() );
        }

        public Group build()
        {
            validate();
            return new Group( this );
        }
    }
}
