package com.enonic.wem.core.account;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.enonic.wem.api.account.result.AccountFacet;
import com.enonic.wem.api.account.result.AccountFacets;

public class AccountFacetsImpl
    implements AccountFacets
{

    final Map<String, AccountFacet> facetsMap;

    AccountFacetsImpl()
    {
        facetsMap = Maps.newHashMap();
    }

    @Override
    public AccountFacet getFacet( final String name )
    {
        return facetsMap.get( name );
    }

    @Override
    public List<AccountFacet> getFacets()
    {
        return Lists.newArrayList( facetsMap.values() );
    }

    @Override
    public Map<String, AccountFacet> asMap()
    {
        return Maps.newHashMap( facetsMap );
    }

    @Override
    public Iterator<AccountFacet> iterator()
    {
        return this.facetsMap.values().iterator();
    }

    public void addFacet( final AccountFacet facet )
    {
        this.facetsMap.put( facet.getName(), facet );
    }
}
