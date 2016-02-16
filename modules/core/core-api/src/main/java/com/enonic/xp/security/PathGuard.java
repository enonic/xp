package com.enonic.xp.security;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;

@Beta
public final class PathGuard
{
    private final String key;

    private final String displayName;

    private final UserStoreAuthConfig authConfig;

    private final ImmutableSet<String> paths;

    public PathGuard( final Builder builder )
    {
        this.key = builder.key;
        this.displayName = builder.displayName;
        this.authConfig = builder.authConfig;
        this.paths = builder.paths.build();
    }

    public String getKey()
    {
        return key;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public UserStoreAuthConfig getAuthConfig()
    {
        return authConfig;
    }

    public ImmutableSet<String> getPaths()
    {
        return paths;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private String key;

        private String displayName;

        private UserStoreAuthConfig authConfig;

        private ImmutableSet.Builder<String> paths = ImmutableSet.builder();

        private Builder()
        {
        }

        public Builder key( final String value )
        {
            this.key = value;
            return this;
        }

        public Builder displayName( final String value )

        {
            this.displayName = value;
            return this;
        }

        public Builder authConfig( final UserStoreAuthConfig value )
        {
            this.authConfig = value;
            return this;
        }

        public Builder addPath( final String value )
        {
            this.paths.add( value );
            return this;
        }

        public PathGuard build()
        {
            return new PathGuard( this );
        }
    }

}
