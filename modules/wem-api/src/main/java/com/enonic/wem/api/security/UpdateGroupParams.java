package com.enonic.wem.api.security;

import com.google.common.base.Preconditions;

import static com.google.common.base.Preconditions.checkNotNull;

public final class UpdateGroupParams
{
    private final PrincipalKey key;

    private final String displayName;

    private UpdateGroupParams( final Builder builder )
    {
        this.key = checkNotNull( builder.principalKey, "groupKey is required for a group" );
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

    public Group update( final Group source )
    {
        Group.Builder result = Group.create( source );
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

    public static Builder create( final Group group )
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
            Preconditions.checkArgument( value.isGroup(), "Invalid PrincipalType for group key: " + value.getType() );
            this.principalKey = value;
            return this;
        }

        public Builder displayName( final String value )
        {
            this.displayName = value;
            return this;
        }

        public UpdateGroupParams build()
        {
            return new UpdateGroupParams( this );
        }
    }
}
