package com.enonic.wem.core.search.account;

import java.util.Arrays;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.core.search.SearchSortOrder;

public final class AccountSearchQuery
{
    private String query;

    private int from;

    private int count;

    private String[] userStore;

    private boolean users;

    private boolean groups;

    private boolean roles;

    private SearchSortOrder sortOrder;

    private AccountIndexField sortField;

    private boolean includeResults;

    private boolean includeFacets;

    private String[] organization;

    private String email;

    private AccountKeys membershipsAccounts;

    public AccountSearchQuery()
    {
        this.query = "";
        this.from = -1;
        this.count = -1;
        this.userStore = null;
        this.organization = null;
        this.users = true;
        this.groups = true;
        this.roles = true;
        this.sortField = null;
        this.includeResults = true;
        this.includeFacets = true;
    }

    public boolean getGroups()
    {
        return this.groups;
    }

    public AccountSearchQuery groups( boolean selectGroups )
    {
        this.groups = selectGroups;
        return this;
    }

    public boolean getUsers()
    {
        return this.users;
    }

    public AccountSearchQuery users( boolean selectUsers )
    {
        this.users = selectUsers;
        return this;
    }

    public String getQuery()
    {
        return this.query != null ? this.query : "";
    }

    public AccountSearchQuery query( String value )
    {
        this.query = value;
        return this;
    }

    public int getFrom()
    {
        return this.from;
    }

    public AccountSearchQuery from( int from )
    {
        this.from = from;
        return this;
    }

    public int getCount()
    {
        return this.count;
    }

    public AccountSearchQuery count( int count )
    {
        this.count = count;
        return this;
    }

    public String[] getUserStores()
    {
        return userStore;
    }

    public AccountSearchQuery userStores( String... userStore )
    {
        if ( userStore == null || userStore.length == 0 || userStore.length == 1 && userStore[0].equals( "" ) )
        {
            this.userStore = null;
        }
        else
        {
            this.userStore = userStore;
        }
        return this;
    }

    public String[] getOrganizations()
    {
        return organization;
    }

    public AccountSearchQuery organizations( String... organizations )
    {
        if ( organizations == null || organizations.length == 0 || organizations.length == 1 && organizations[0].equals( "" ) )
        {
            this.organization = null;
        }
        else
        {
            this.organization = Arrays.<String>copyOf( organizations, organizations.length );
        }
        return this;
    }

    public SearchSortOrder getSortOrder()
    {
        return sortOrder;
    }

    public AccountSearchQuery sortOrder( SearchSortOrder sortOrder )
    {
        this.sortOrder = sortOrder;
        return this;
    }

    public AccountIndexField getSortField()
    {
        return sortField;
    }

    public AccountSearchQuery sortField( AccountIndexField sortField )
    {
        this.sortField = sortField;
        return this;
    }

    public boolean isIncludeResults()
    {
        return includeResults;
    }

    public AccountSearchQuery includeResults( boolean includeResults )
    {
        this.includeResults = includeResults;
        return this;
    }

    public boolean isIncludeFacets()
    {
        return includeFacets;
    }

    public AccountSearchQuery includeFacets( boolean includeFacets )
    {
        this.includeFacets = includeFacets;
        return this;
    }

    public boolean getRoles()
    {
        return roles;
    }

    public AccountSearchQuery roles( boolean roles )
    {
        this.roles = roles;
        return this;
    }

    public String getEmail()
    {
        return email;
    }

    public AccountSearchQuery email( final String email )
    {
        this.email = email;
        return this;
    }

    public AccountSearchQuery membershipsFor( final AccountKeys accounts )
    {
        this.membershipsAccounts = accounts;
        return this;
    }

    public AccountSearchQuery membershipsFor( final AccountKey account )
    {
        this.membershipsAccounts = AccountKeys.from( account );
        return this;
    }

    public AccountKeys getMembershipsFor()
    {
        return this.membershipsAccounts;
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

        final AccountSearchQuery that = (AccountSearchQuery) o;

        if ( count != that.count )
        {
            return false;
        }
        if ( from != that.from )
        {
            return false;
        }
        if ( groups != that.groups )
        {
            return false;
        }
        if ( includeFacets != that.includeFacets )
        {
            return false;
        }
        if ( includeResults != that.includeResults )
        {
            return false;
        }
        if ( roles != that.roles )
        {
            return false;
        }
        if ( users != that.users )
        {
            return false;
        }
        if ( !Arrays.equals( organization, that.organization ) )
        {
            return false;
        }
        if ( query != null ? !query.equals( that.query ) : that.query != null )
        {
            return false;
        }
        if ( sortField != that.sortField )
        {
            return false;
        }
        if ( sortOrder != that.sortOrder )
        {
            return false;
        }
        if ( !Arrays.equals( userStore, that.userStore ) )
        {
            return false;
        }
        if ( email != null ? !email.equals( that.email ) : that.email != null )
        {
            return false;
        }
        if ( membershipsAccounts != null ? !membershipsAccounts.equals( that.membershipsAccounts ) : that.membershipsAccounts != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = query != null ? query.hashCode() : 0;
        result = 31 * result + from;
        result = 31 * result + count;
        result = 31 * result + ( userStore != null ? Arrays.hashCode( userStore ) : 0 );
        result = 31 * result + ( users ? 1 : 0 );
        result = 31 * result + ( groups ? 1 : 0 );
        result = 31 * result + ( roles ? 1 : 0 );
        result = 31 * result + ( sortOrder != null ? sortOrder.hashCode() : 0 );
        result = 31 * result + ( sortField != null ? sortField.hashCode() : 0 );
        result = 31 * result + ( includeResults ? 1 : 0 );
        result = 31 * result + ( includeFacets ? 1 : 0 );
        result = 31 * result + ( organization != null ? Arrays.hashCode( organization ) : 0 );
        result = 31 * result + ( email != null ? email.hashCode() : 0 );
        result = 31 * result + ( membershipsAccounts != null ? membershipsAccounts.hashCode() : 0 );
        return result;
    }
}
