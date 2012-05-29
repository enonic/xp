package com.enonic.wem.core.search.account;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.enonic.wem.core.search.Facets;


public final class AccountSearchResults
    implements Iterable<AccountSearchHit>
{
    private final int from;
    private final int total;
    private final List<AccountSearchHit> hits;
    private final Facets facets;

    public AccountSearchResults( int from, int total )
    {
        this.from = from;
        this.total = total;
        this.hits = new ArrayList<AccountSearchHit>();
        this.facets = new Facets();
    }

    public int getCount()
    {
        return this.hits.size();
    }

    public int getTotal()
    {
        return this.total;
    }

    public int getFrom()
    {
        return this.from;
    }

    public void add(AccountSearchHit hit)
    {
        if (hit != null) {
            this.hits.add(hit);
        }
    }

    public void add( AccountKey key, AccountType accountType, float score )
    {
        add( new AccountSearchHit( key, accountType, score ) );
    }

    public Iterator<AccountSearchHit> iterator()
    {
        return this.hits.iterator();
    }

    public Facets getFacets()
    {
        return facets;
    }
}
