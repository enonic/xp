package com.enonic.wem.core.index.accountfacet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Deprecated
public final class AccountFacets
    implements Iterable<AccountFacet>
{
    private final List<AccountFacet> accountFacets;

    public AccountFacets()
    {
        this.accountFacets = new ArrayList<AccountFacet>();
    }

    public void addFacet( AccountFacet accountFacet )
    {
        this.accountFacets.add( accountFacet );
    }

    public int getCount()
    {
        return this.accountFacets.size();
    }

    public Iterator<AccountFacet> iterator()
    {
        return this.accountFacets.iterator();
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

        final AccountFacets accountFacets1 = (AccountFacets) o;

        if ( accountFacets != null ? !accountFacets.equals( accountFacets1.accountFacets ) : accountFacets1.accountFacets != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return accountFacets != null ? accountFacets.hashCode() : 0;
    }

    public void consolidate()
    {
        for ( AccountFacet accountFacet : accountFacets )
        {
            accountFacet.consolidate();
        }
    }
}
