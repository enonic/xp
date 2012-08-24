package com.enonic.wem.core.account;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.account.result.AccountFacet;

public class AccountFacetImpl
    implements AccountFacet
{
    private final String name;

    private final List<Entry> entries;

    AccountFacetImpl( final String name, final List<Entry> entries )
    {
        this.name = name;
        this.entries = ImmutableList.copyOf( entries );
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public List<Entry> getEntries()
    {
        return this.entries;
    }

    @Override
    public Iterator<Entry> iterator()
    {
        return this.entries.iterator();
    }

}