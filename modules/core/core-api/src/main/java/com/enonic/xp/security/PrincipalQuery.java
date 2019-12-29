package com.enonic.xp.security;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class PrincipalQuery
{
    public static final int DEFAULT_SIZE = 10;

    private static final int GET_ALL_SIZE_FLAG = -1;

    private static final ImmutableSet<PrincipalType> ALL_TYPES = Sets.immutableEnumSet( EnumSet.allOf( PrincipalType.class ) );

    private final int from;

    private final int size;

    private final ImmutableSet<PrincipalType> principalTypes;

    private final IdProviderKeys idProviders;

    private final String searchText;

    private final String email;

    private final String name;

    private final String displayName;

    public PrincipalQuery( final Builder builder )
    {
        from = builder.from;
        size = builder.size;
        searchText = builder.searchText;
        email = builder.email;
        name = builder.name;
        displayName = builder.displayName;
        idProviders = IdProviderKeys.from( builder.idProviders.build() );
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

    public IdProviderKeys getIdProviders()
    {
        return idProviders;
    }

    public String getSearchText()
    {
        return searchText;
    }

    public String getEmail()
    {
        return email;
    }

    public String getName()
    {
        return name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final PrincipalQuery that = (PrincipalQuery) o;
        return from == that.from &&
            size == that.size &&
            Objects.equals( principalTypes, that.principalTypes ) && Objects.equals( idProviders, that.idProviders ) &&
            Objects.equals( searchText, that.searchText ) &&
            Objects.equals( email, that.email ) &&
            Objects.equals( name, that.name ) &&
            Objects.equals( displayName, that.displayName );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( from, size, principalTypes, idProviders, searchText, email, name, displayName );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private int from = 0;

        private int size = DEFAULT_SIZE;

        private final EnumSet<PrincipalType> principalTypes;

        private final ImmutableList.Builder<IdProviderKey> idProviders;

        private String searchText;

        private String email;

        private String name;

        private String displayName;

        private Builder()
        {
            this.principalTypes = EnumSet.noneOf( PrincipalType.class );
            this.idProviders = ImmutableList.builder();
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
            for ( PrincipalType principalType : principalTypes )
            {
                this.principalTypes.add( principalType );
            }
            return this;
        }

        public Builder idProvider( final IdProviderKey idProviderKey )
        {
            this.idProviders.add( idProviderKey );
            return this;
        }

        public Builder idProviders( final Iterable<IdProviderKey> idProviderKeys )
        {
            this.idProviders.addAll( idProviderKeys );
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

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder displayName( final String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public PrincipalQuery build()
        {
            return new PrincipalQuery( this );
        }
    }

}
