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
        this.sortField = field;
        this.sortDirection = Direction.ASC;
        return this;
    }

    public AccountQuery sortDesc( final String field )
    {
        this.sortField = field;
        this.sortDirection = Direction.DESC;
        return this;
    }
}
