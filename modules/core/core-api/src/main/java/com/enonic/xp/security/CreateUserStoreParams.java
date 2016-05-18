package com.enonic.xp.security;

import com.google.common.annotations.Beta;

import com.enonic.xp.security.acl.UserStoreAccessControlList;

import static com.google.common.base.Preconditions.checkNotNull;

@Beta
public final class CreateUserStoreParams
{

    private final UserStoreKey userStoreKey;

    private final String displayName;

    private final UserStoreAccessControlList userStorePermissions;

    private final String description;

    private CreateUserStoreParams( final Builder builder )
    {
        this.userStoreKey = checkNotNull( builder.userStoreKey, "userStoreKey is required" );
        this.displayName = checkNotNull( builder.displayName, "displayName is required" );
        this.userStorePermissions =
            builder.userStorePermissions == null ? UserStoreAccessControlList.empty() : builder.userStorePermissions;
        this.description = builder.description;
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
        private UserStoreKey userStoreKey;

        private String displayName;

        private UserStoreAccessControlList userStorePermissions;

        private String description;

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

        public Builder description( final String value )
        {
            this.description = value;
            return this;
        }

        public CreateUserStoreParams build()
        {
            return new CreateUserStoreParams( this );
        }
    }
}
