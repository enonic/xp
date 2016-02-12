package com.enonic.xp.admin.impl.market;

import com.squareup.okhttp.Response;

public interface MarketDataProvider
{
    Response fetch( final String url, final String version );
}
