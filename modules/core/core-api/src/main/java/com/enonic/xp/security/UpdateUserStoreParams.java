package com.enonic.xp.security;

import com.google.common.annotations.Beta;

import com.enonic.xp.security.acl.UserStoreAccessControlList;

import static com.google.common.base.Preconditions.checkNotNull;

@Beta
public final class UpdateUserStoreParams
{

    private final UserStoreKey userStoreKey;

    private final String displayName;

    private final String description;

    private final AuthConfig authConfig;

    private final UserStoreAccessControlList userStorePermissions;

    private UpdateUserStoreParams( final Builder builder )
    {
        this.userStoreKey = checkNotNull( builder.userStoreKey, "userStoreKey is required" );
        this.displayName = checkNotNull( builder.displayName, "displayName is required" ).trim();
        this.description = builder.description;
        this.authConfig = builder.authConfig;
        this.userStorePermissions = builder.userStorePermissions;
    }

    public UserStoreKey getKey()
    {
        return userStoreKey;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getDescription()
    {
        return description;
    }

    public AuthConfig getAuthConfig()
    {
        return authConfig;
    }

    public UserStoreAccessControlList getUserStorePermissions()
    {
        return userStorePermissions;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private UserStoreKey userStoreKey;

        private String displayName;

        private String description;

        private AuthConfig authConfig;

        private UserStoreAccessControlList userStorePermissions;

        private Builder()
        {
        }

        public Builder key( final UserStoreKey value )
        {
            this.userStoreKey = value;
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

        public Builder authConfig( final AuthConfig value )
        {
            this.authConfig = value;
            return this;
        }

        public Builder permissions( final UserStoreAccessControlList permissions )
        {
            this.userStorePermissions = permissions;
            return this;
        }

        public UpdateUserStoreParams build()
        {
            return new UpdateUserStoreParams( this );
        }
    }
}
