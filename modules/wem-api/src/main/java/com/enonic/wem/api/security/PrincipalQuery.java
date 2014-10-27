package com.enonic.wem.api.security;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public final class PrincipalQuery
{
    private static final int DEFAULT_SIZE = 10;

    private static final ImmutableSet<PrincipalType> ALL_TYPES = Sets.immutableEnumSet( EnumSet.allOf( PrincipalType.class ) );

    private final int from;

    private final int size;

    private final ImmutableSet<PrincipalType> principalTypes;

    private final UserStoreKeys userStores;

    public PrincipalQuery( final Builder builder )
    {
        this.from = builder.from;
        this.size = builder.size;
        this.userStores = UserStoreKeys.from( builder.userStores.build() );
        if ( builder.principalTypes.isEmpty() )
        {
            principalTypes = ALL_TYPES;
        }
        else
        {
            principalTypes = Sets.immutableEnumSet( builder.principalTypes );
        }
    }

    public int getFrom()
    {
        return from;
    }

    public int getSize()
    {
        return size;
    }

    public Set<PrincipalType> getPrincipalTypes()
    {
        return principalTypes;
    }

    public UserStoreKeys getUserStores()
    {
        return userStores;
    }

    public static Builder newQuery()
    {
        return new Builder();
    }

    public static class Builder
    {
        private int from = 0;

        private int size = DEFAULT_SIZE;

        private final EnumSet<PrincipalType> principalTypes;

        private final ImmutableList.Builder<UserStoreKey> userStores;

        private Builder()
        {
            this.principalTypes = EnumSet.noneOf( PrincipalType.class );
            this.userStores = ImmutableList.builder();
        }

        public Builder from( final int from )
        {
            this.from = from;
            return this;
        }

        public Builder size( final int size )
        {
            this.size = size;
            return this;
        }

        public Builder includeUsers()
        {
            this.principalTypes.add( PrincipalType.USER );
            return this;
        }

        public Builder includeGroups()
        {
            this.principalTypes.add( PrincipalType.GROUP );
            return this;
        }

        public Builder includeRoles()
        {
            this.principalTypes.add( PrincipalType.ROLE );
            return this;
        }

        public Builder includeTypes( final PrincipalType... principalTypes )
        {
            Collections.addAll( this.principalTypes, principalTypes );
            return this;
        }

        public Builder userStore( final UserStoreKey userStoreKey )
        {
            this.userStores.add( userStoreKey );
            return this;
        }

        public Builder userStores( final Iterable<UserStoreKey> userStoreKeys )
        {
            this.userStores.addAll( userStoreKeys );
            return this;
        }

        public PrincipalQuery build()
        {
            return new PrincipalQuery( this );
        }
    }

}
