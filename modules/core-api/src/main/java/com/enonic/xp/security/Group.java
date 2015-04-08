package com.enonic.xp.security;

import com.google.common.annotations.Beta;

import static com.google.common.base.Preconditions.checkArgument;

@Beta
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

    public static class Builder
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
            checkArgument( this.key.isGroup(), "Invalid Principal Type for Group: " + this.key.getType() );
        }

        public Group build()
        {
            validate();
            return new Group( this );
        }
    }
}
