package com.enonic.wem.api.account.query;

import java.util.Iterator;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.result.AccountFacets;

public final class AccountQueryHits
    implements Iterable<AccountKey>
{
    private final int totalSize;

    private final AccountKeys keys;

    private AccountFacets facets;

    public AccountQueryHits( final int totalSize, final AccountKeys keys )
    {
        this.totalSize = totalSize;
        this.keys = keys;
    }

    public AccountKeys getKeys()
    {
        return this.keys;
    }

    public int getSize()
    {
        return this.keys.getSize();
    }

    public int getTotalSize()
    {
        return this.totalSize;
    }

    public boolean isEmpty()
    {
        return this.keys.isEmpty();
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
    public Iterator<AccountKey> iterator()
    {
        return this.keys.iterator();
    }
}
