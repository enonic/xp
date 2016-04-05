package com.enonic.xp.security;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import static com.google.common.base.Preconditions.checkNotNull;

@Beta
public final class CreateGroupParams
{
    private final PrincipalKey key;

    private final String displayName;

    private final String description;

    private CreateGroupParams( final Builder builder )
    {
        this.key = checkNotNull( builder.principalKey, "groupKey is required for a group" );
        this.displayName = checkNotNull( builder.displayName, "displayName is required for a group" );
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

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private PrincipalKey principalKey;

        private String displayName;

        private String description;

        private Builder()
        {
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

        public Builder description( final String value )
        {
            this.description = value;
            return this;
        }

        public CreateGroupParams build()
        {
            return new CreateGroupParams( this );
        }
    }
}
