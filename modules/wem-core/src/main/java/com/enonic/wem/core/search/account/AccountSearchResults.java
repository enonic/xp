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

    public void add( AccountSearchHit hit )
    {
        if ( hit != null )
        {
            this.hits.add( hit );
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

        final AccountSearchResults that = (AccountSearchResults) o;

        if ( from != that.from )
        {
            return false;
        }
        if ( total != that.total )
        {
            return false;
        }
        if ( facets != null ? !facets.equals( that.facets ) : that.facets != null )
        {
            return false;
        }
        if ( hits != null ? !hits.equals( that.hits ) : that.hits != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = from;
        result = 31 * result + total;
        result = 31 * result + ( hits != null ? hits.hashCode() : 0 );
        result = 31 * result + ( facets != null ? facets.hashCode() : 0 );
        return result;
    }
}
