package com.enonic.wem.api.account.query;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public final class AccountFacets
    implements Iterable<AccountFacet>
{
    private final Map<String, AccountFacet> map;

    public AccountFacets()
    {
        this.map = Maps.newHashMap();
    }

    public AccountFacet getFacet( final String name )
    {
        return this.map.get( name );
    }

    public List<AccountFacet> getFacets()
    {
        return ImmutableList.copyOf( this.map.values() );
    }

    public Map<String, AccountFacet> asMap()
    {
        return ImmutableMap.copyOf( this.map );
    }

    @Override
    public Iterator<AccountFacet> iterator()
    {
        return this.map.values().iterator();
    }

    public void addFacet( final AccountFacet facet )
    {
        this.map.put( facet.getName(), facet );
    }
}
