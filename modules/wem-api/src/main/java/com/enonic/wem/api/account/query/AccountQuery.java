package com.enonic.wem.api.account.query;

import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import com.enonic.wem.api.account.AccountType;
import com.enonic.wem.api.account.selector.AccountSelector;

public final class AccountQuery
    implements AccountSelector
{
    public enum Direction
    {
        ASC,
        DESC
    }

    private final String query;

    private Set<AccountType> types;

    private Set<String> userStores;

    private int limit;

    private int offset;

    private String sortField;

    private Direction sortDirection;

    private String email;

    public AccountQuery()
    {
        this( null );
    }

    public AccountQuery( final String query )
    {
        this.query = query != null ? query : "";
        this.types = ImmutableSet.copyOf( AccountType.values() );
        this.userStores = ImmutableSet.of();
        this.sortDirection = Direction.ASC;
        this.offset = 0;
        this.limit = 10;
    }

    public String getQuery()
    {
        return this.query;
    }

    public Set<AccountType> getTypes()
    {
        return this.types;
    }

    public Set<String> getUserStores()
    {
        return this.userStores;
    }

    public int getLimit()
    {
        return this.limit;
    }

    public int getOffset()
    {
        return this.offset;
    }

    public String getSortField()
    {
        return this.sortField;
    }

    public Direction getSortDirection()
    {
        return this.sortDirection;
    }

    public String getEmail()
    {
        return email;
    }

    public AccountQuery types( final AccountType... values )
    {
        this.types = ImmutableSet.copyOf( values );
        return this;
    }

    public AccountQuery userStores( final String... values )
    {
        this.userStores = ImmutableSet.copyOf( values );
        return this;
    }

    public AccountQuery limit( final int value )
    {
        Preconditions.checkArgument( value >= 0, "Limit must be >= 0" );
        this.limit = value;
        return this;
    }

    public AccountQuery offset( final int value )
    {
        Preconditions.checkArgument( value >= 0, "Offset must be >= 0" );
        this.offset = value;
        return this;
    }

    public AccountQuery sortAsc( final String field )
    {
        return sort( field, true );
    }

    public AccountQuery sortDesc( final String field )
    {
        return sort( field, false );
    }

    public AccountQuery sort( final String field, final boolean asc )
    {
        this.sortField = field;
        this.sortDirection = asc ? Direction.ASC : Direction.DESC;
        return this;
    }

    public AccountQuery email( final String email )
    {
        this.email = email;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof AccountQuery ) )
        {
            return false;
        }

        final AccountQuery that = (AccountQuery) o;
        return Objects.equal( this.limit, that.limit ) &&
            Objects.equal( this.offset, that.offset ) &&
            Objects.equal( this.email, that.email ) &&
            Objects.equal( this.query, that.query ) &&
            Objects.equal( this.sortDirection, that.sortDirection ) &&
            Objects.equal( this.sortField, that.sortField ) &&
            Objects.equal( this.types, that.types ) &&
            Objects.equal( this.userStores, that.userStores );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.query, this.types, this.userStores, this.limit, this.offset, this.sortField, this.sortDirection,
                                 this.email );
    }
}
