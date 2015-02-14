package com.enonic.xp.core.security;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public final class PrincipalQuery
{
    private static final int DEFAULT_SIZE = 10;

    private static final int GET_ALL_SIZE_FLAG = -1;

    private static final ImmutableSet<PrincipalType> ALL_TYPES = Sets.immutableEnumSet( EnumSet.allOf( PrincipalType.class ) );

    private final int from;

    private final int size;

    private final ImmutableSet<PrincipalType> principalTypes;

    private final UserStoreKeys userStores;

    private final String searchText;

    private final String email;

    public PrincipalQuery( final Builder builder )
    {
        from = builder.from;
        size = builder.size;
        searchText = builder.searchText;
        email = builder.email;
        userStores = UserStoreKeys.from( builder.userStores.build() );
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

    public String getSearchText()
    {
        return searchText;
    }

    public String getEmail()
    {
        return email;
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

        private String searchText;

        private String email;

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

        public Builder getAll()
        {
            this.size = GET_ALL_SIZE_FLAG;
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

        public Builder includeTypes( final Iterable<PrincipalType> principalTypes )
        {
            Iterables.addAll( this.principalTypes, principalTypes );
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

        public Builder searchText( final String searchText )
        {
            this.searchText = searchText;
            return this;
        }

        public Builder email( final String email )
        {
            this.email = email;
            return this;
        }

        public PrincipalQuery build()
        {
            return new PrincipalQuery( this );
        }
    }

}
