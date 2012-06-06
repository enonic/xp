package com.enonic.cms.web.rest.account;

import java.util.ArrayList;
import java.util.List;

public class AccountsModel
{
    private int total;

    private final List<AccountModel> accounts;

    private final List<SearchFacetModel> facets;

    public AccountsModel()
    {
        this.accounts = new ArrayList<AccountModel>();
        this.facets = new ArrayList<SearchFacetModel>();
    }

    public int getTotal()
    {
        return total;
    }

    public void setTotal( int total )
    {
        this.total = total;
    }

    public List<AccountModel> getAccounts()
    {
        return accounts;
    }

    public void addAccount( AccountModel account )
    {
        this.accounts.add( account );
    }

    public List<SearchFacetModel> getFacets()
    {
        return facets;
    }

    public void addFacet( SearchFacetModel facet )
    {
        this.facets.add( facet );
    }

}
