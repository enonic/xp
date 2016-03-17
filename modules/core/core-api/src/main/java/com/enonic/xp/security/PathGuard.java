package com.enonic.xp.security;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;

@Beta
public final class PathGuard
{
    private final PathGuardKey key;

    private final String displayName;

    private final String description;

    private final UserStoreKey userStoreKey;

    private final boolean passive;

    private final ImmutableSet<String> paths;

    public PathGuard( final Builder builder )
    {
        this.key = builder.key;
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.userStoreKey = builder.userStoreKey;
        this.passive = builder.passive;
        this.paths = builder.paths.build();
    }

    public PathGuardKey getKey()
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

    public UserStoreKey getUserStoreKey()
    {
        return userStoreKey;
    }

    public boolean isPassive()
    {
        return passive;
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
        private PathGuardKey key;

        private String displayName;

        private String description;

        private UserStoreKey userStoreKey;

        private boolean passive;

        private ImmutableSet.Builder<String> paths = ImmutableSet.builder();

        private Builder()
        {
        }

        public Builder key( final PathGuardKey value )
        {
            this.key = value;
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

        public Builder userStoreKey( final UserStoreKey value )
        {
            this.userStoreKey = value;
            return this;
        }

        public Builder passive( final boolean value )
        {
            this.passive = value;
            return this;
        }

        public Builder addPath( final String value )
        {
            this.paths.add( value );
            return this;
        }

        public Builder addAllPaths( final Iterable<String> value )
        {
            this.paths.addAll( value );
            return this;
        }


        public PathGuard build()
        {
            return new PathGuard( this );
        }
    }

}
