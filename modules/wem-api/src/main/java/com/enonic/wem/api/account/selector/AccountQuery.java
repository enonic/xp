package com.enonic.wem.api.account.selector;

import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import com.enonic.wem.api.account.AccountType;

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
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final AccountQuery that = (AccountQuery) o;

        if ( limit != that.limit )
        {
            return false;
        }
        if ( offset != that.offset )
        {
            return false;
        }
        if ( email != null ? !email.equals( that.email ) : that.email != null )
        {
            return false;
        }
        if ( query != null ? !query.equals( that.query ) : that.query != null )
        {
            return false;
        }
        if ( sortDirection != that.sortDirection )
        {
            return false;
        }
        if ( sortField != null ? !sortField.equals( that.sortField ) : that.sortField != null )
        {
            return false;
        }
        if ( types != null ? !types.equals( that.types ) : that.types != null )
        {
            return false;
        }
        if ( userStores != null ? !userStores.equals( that.userStores ) : that.userStores != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = query != null ? query.hashCode() : 0;
        result = 31 * result + ( types != null ? types.hashCode() : 0 );
        result = 31 * result + ( userStores != null ? userStores.hashCode() : 0 );
        result = 31 * result + limit;
        result = 31 * result + offset;
        result = 31 * result + ( sortField != null ? sortField.hashCode() : 0 );
        result = 31 * result + ( sortDirection != null ? sortDirection.hashCode() : 0 );
        result = 31 * result + ( email != null ? email.hashCode() : 0 );
        return result;
    }
}
