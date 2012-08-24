package com.enonic.wem.core.account;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeySet;
import com.enonic.wem.api.account.result.AccountFacets;
import com.enonic.wem.api.account.result.AccountResult;

class AccountResultImpl
    implements AccountResult
{
    private int totalSize;

    private AccountKeySet accountKeySet;

    private List<Account> accounts;

    private AccountFacets facets;

    AccountResultImpl()
    {
        totalSize = 0;
        accountKeySet = AccountKeySet.empty();
        accounts = Collections.emptyList();
        facets = new AccountFacetsImpl();
    }

    @Override
    public int getSize()
    {
        return this.accounts.size();
    }

    @Override
    public int getTotalSize()
    {
        return this.totalSize;
    }

    @Override
    public boolean isEmpty()
    {
        return this.accounts.isEmpty();
    }

    @Override
    public Account first()
    {
        return this.accounts.get( 0 );
    }

    @Override
    public Account firstOrNull()
    {
        return this.accounts.isEmpty() ? null : this.accounts.get( 0 );
    }

    @Override
    public AccountKeySet asKeySet()
    {
        return this.accountKeySet;
    }

    @Override
    public AccountFacets getFacets()
    {
        return this.facets;
    }

    @Override
    public Iterator<Account> iterator()
    {
        return this.accounts.iterator();
    }

    public void setTotalSize( final int totalSize )
    {
        this.totalSize = totalSize;
    }

    public void setAccounts( final List<Account> accounts )
    {
        if ( accounts == null )
        {
            this.accounts = Collections.emptyList();
            accountKeySet = AccountKeySet.empty();
        }
        else
        {
            this.accounts = accounts;
            final Collection<AccountKey> keyList = Collections2.transform( accounts, new ExtractAccountKeyFunction() );
            accountKeySet = AccountKeySet.from( keyList );
        }
    }

    public void setFacets( final AccountFacets facets )
    {
        this.facets = facets;
    }

    private final static class ExtractAccountKeyFunction
        implements Function<Account, AccountKey>
    {
        @Override
        public AccountKey apply( final Account value )
        {
            return value.getKey();
        }
    }
}
