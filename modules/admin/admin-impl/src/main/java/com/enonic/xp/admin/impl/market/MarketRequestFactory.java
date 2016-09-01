package com.enonic.xp.admin.impl.market;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Request;

class MarketRequestFactory
{

    public static Request create( final String baseUrl, final String version, final int start, final int count )
    {
        Map<String, Object> params = Maps.newHashMap();

        params.put( "xpVersion", version );
        params.put( "start", start );
        params.put( "count", count );

        return create( baseUrl, params );
    }

    public static Request create( final String baseUrl, final List<String> ids )
    {

        Map<String, Object> params = Maps.newHashMap();
        params.put( "ids", ids );

        return create( baseUrl, params );
    }

    private static Request create( final String baseUrl, Map<String, Object> params )
    {
        final Request.Builder request = new Request.Builder();
        request.url( baseUrl );

        HttpUrl url = HttpUrl.parse( baseUrl );

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
