package com.enonic.xp.core.security;

import com.enonic.xp.core.security.acl.UserStoreAccessControlList;

import static com.google.common.base.Preconditions.checkNotNull;

public final class UpdateUserStoreParams
{

    private final UserStoreKey userStoreKey;

    private final String displayName;

    private final UserStoreAccessControlList userStorePermissions;

    private UpdateUserStoreParams( final Builder builder )
    {
        this.userStoreKey = checkNotNull( builder.userStoreKey, "userStoreKey is required" );
        this.displayName = checkNotNull( builder.displayName, "displayName is required" );
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
