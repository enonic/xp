package com.enonic.xp.admin.impl.market;

import com.squareup.okhttp.Response;

public interface MarketDataProvider
{
    Response fetch( String url, String proxy, String version, int start, int count );
}
