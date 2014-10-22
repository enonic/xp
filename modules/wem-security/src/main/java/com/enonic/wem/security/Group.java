package com.enonic.wem.security;

import static com.google.common.base.Preconditions.checkArgument;

public final class Group
    extends Principal
{
    private Group( final Builder builder )
    {
        super( builder.principalKey, builder.displayName );
        checkArgument( builder.principalKey.isGroup(), "Invalid Principal Type for Group: " + builder.principalKey.getType() );
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
        private PrincipalKey principalKey;

        private String displayName;

        private Builder()
        {
        }

        private Builder( final Group group )
        {
            this.principalKey = group.getKey();
            this.displayName = group.getDisplayName();
        }

        public Builder groupKey( final PrincipalKey value )
        {
            this.principalKey = value;
            return this;
        }

        public Builder displayName( final String value )
        {
            this.displayName = value;
            return this;
        }

        public Group build()
        {
            return new Group( this );
        }
    }
}
