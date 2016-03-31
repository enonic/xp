package com.enonic.xp.admin.impl.market;

import java.util.Map;

import com.google.common.collect.Maps;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Request;

class MarketRequestFactory
{
    private final static Map<String, Object> params = Maps.newHashMap();

    public static Request create( final String baseUrl, final String version, final int start, final int count )
    {
        final Request.Builder request = new Request.Builder();
        request.url( baseUrl );

        HttpUrl url = HttpUrl.parse( baseUrl );

        params.put( "xpVersion", version );
        params.put( "start", start );
        params.put( "count", count );

        url = addParams( url, params );

        request.url( url );

        request.header( "Accept", "application/json" );

        request.get();

        return request.build();
    }

    private static HttpUrl addParams( final HttpUrl url, final Map<String, Object> params )
    {
        HttpUrl.Builder urlBuilder = url.newBuilder();
        for ( Map.Entry<String, Object> header : params.entrySet() )
        {
            if ( header.getValue() != null )
            {
                urlBuilder.addEncodedQueryParameter( header.getKey(), header.getValue().toString() );
            }
        }
        return urlBuilder.build();
    }
}
