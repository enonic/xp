package com.enonic.xp.admin.impl.market;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class MarketServiceImplTest
{
    private MarketServiceImpl service;

    private MarketDataHttpProvider provider;

    @BeforeEach
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
        this.service.get( new ArrayList<>(), "newest", 0, 10 );
        Mockito.verify( provider, Mockito.times( 1 ) ).search( new ArrayList<>(), "newest", 0, 10 );
    }
}
