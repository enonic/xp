package com.enonic.xp.admin.impl.market;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

public class MarketServiceImplTest
{
    private MarketServiceImpl service;

    private MarketDataHttpProvider provider;

    @Before
    public void setUp()
        throws Exception
    {
        this.service = new MarketServiceImpl();
        this.provider = Mockito.mock( MarketDataHttpProvider.class );
        this.service.setProvider( this.provider );
    }

    @Test
    public void test_provider_search_is_called()
        throws Exception
    {
        this.service.get( Lists.newArrayList(), "newest", 0, 10 );
        Mockito.verify( provider, Mockito.times( 1 ) ).search( Lists.newArrayList(), "newest", 0, 10 );
    }
}