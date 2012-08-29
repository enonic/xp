package com.enonic.wem.api.account.result;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.account.Account;

public final class AccountResult
    implements Iterable<Account>
{
    private final int totalSize;

    private final List<Account> accounts;

    private AccountFacets facets;

    public AccountResult( final int totalSize, final List<Account> accounts )
    {
        this.totalSize = totalSize;
        this.accounts = ImmutableList.copyOf( accounts );
    }

    public int getSize()
    {
        return this.accounts.size();
    }

    public int getTotalSize()
    {
        return this.totalSize;
    }

    public boolean isEmpty()
    {
        return this.accounts.isEmpty();
    }

    public Account first()
    {
        return this.accounts.isEmpty() ? null : this.accounts.get( 0 );
    }

    public List<Account> getAll()
    {
        return ImmutableList.copyOf( this.accounts );
    }

    public AccountFacets getFacets()
    {
        return this.facets;
    }

    public void setFacets( final AccountFacets facets )
    {
        this.facets = facets;
    }

    @Override
    public Iterator<Account> iterator()
    {
        return this.accounts.iterator();
    }
}
