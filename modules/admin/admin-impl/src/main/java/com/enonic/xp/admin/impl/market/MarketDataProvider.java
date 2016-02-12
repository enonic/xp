package com.enonic.xp.admin.impl.market;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Component;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import com.enonic.xp.market.MarketException;

@Component
public class MarketDataProvider
{
    private static final int connectionTimeout = 10_000;

    private static final int readTimeout = 10_000;

    public Response fetch( final String url, final String version )
    {
        final Request request = MarketRequestFactory.create( url, version );

        final OkHttpClient client = new OkHttpClient();
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
