package com.enonic.wem.api.account.result;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

public final class AccountFacet
    implements Iterable<AccountFacetEntry>
{
    private final String name;

    private final List<AccountFacetEntry> entries;

    public AccountFacet( final String name )
    {
        this.name = name;
        this.entries = Lists.newArrayList();
    }

    public String getName()
    {
        return this.name;
    }

    public List<AccountFacetEntry> getEntries()
    {
        return this.entries;
    }

    @Override
    public Iterator<AccountFacetEntry> iterator()
    {
        return this.entries.iterator();
    }

    public void addEntry( final AccountFacetEntry entry )
    {
        this.entries.add( entry );
    }
}
