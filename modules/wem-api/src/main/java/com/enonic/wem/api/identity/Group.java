package com.enonic.wem.api.identity;

import com.google.common.base.Preconditions;

public final class Group
    extends Identity
{
    private Group( final Builder builder )
    {
        super( builder.identity );
        Preconditions.checkArgument( getIdentityKey().isGroup(), "Invalid Identity Type for Group: " + getIdentityKey().getType() );
    }

    public static Builder newGroup()
    {
        return new Builder();
    }

    public static Builder newGroup( final Group group )
    {
        return new Builder( group );
    }

    public static class Builder
    {
        private Identity.Builder identity;

        private Builder()
        {
            identity = new Identity.Builder();
        }

        private Builder( final Group group )
        {
            identity = new Identity.Builder( group );
        }

        public Builder identityKey( final IdentityKey value )
        {
            this.identity.identityKey( value );
            return this;
        }

        public Builder displayName( final String value )
        {
            this.identity.displayName( value );
            return this;
        }

        public Group build()
        {
            return new Group( this );
        }
    }
}
