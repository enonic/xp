package com.enonic.xp.admin.impl.market;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Component;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import com.enonic.xp.market.MarketException;

import com.squareup.okhttp.HttpUrl;
import java.net.InetSocketAddress;
import java.net.Proxy;

@Component(immediate = true)
public class MarketDataProviderImpl
    implements MarketDataProvider
{
    private static final int connectionTimeout = 10_000;

    private static final int readTimeout = 10_000;

    public Response fetch( final String url, final String proxyUrl, final String version, final int start, final int count )
    {
        final Request request = MarketRequestFactory.create( url, version, start, count );


        final OkHttpClient client = new OkHttpClient();

        HttpUrl parsedProxyUrl = HttpUrl.parse(proxyUrl);
        if (parsedProxyUrl !=null && parsedProxyUrl.host()!=null && !"".equals(parsedProxyUrl.host()) && parsedProxyUrl.scheme()!=null){
            Proxy proxy = new Proxy(Proxy.Type.valueOf(parsedProxyUrl.scheme().toUpperCase()), new InetSocketAddress(parsedProxyUrl.host(), parsedProxyUrl.port()));
            client.setProxy(proxy);
        }

        client.setReadTimeout( readTimeout, TimeUnit.MILLISECONDS );
        client.setConnectTimeout( connectionTimeout, TimeUnit.MILLISECONDS );

        try
        {
            return client.newCall( request ).execute();
        }
        catch ( IOException e )
        {
            throw new MarketException( "Cannot connect to marked", e );
        }
    }

}
