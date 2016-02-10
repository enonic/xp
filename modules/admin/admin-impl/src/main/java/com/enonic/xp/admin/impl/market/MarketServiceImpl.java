package com.enonic.xp.admin.impl.market;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import com.enonic.xp.admin.impl.rest.resource.application.json.MarketApplicationsJson;
import com.enonic.xp.json.ObjectMapperHelper;
import com.enonic.xp.market.MarketException;
import com.enonic.xp.web.HttpStatus;

@Component(immediate = true, configurationPid = "com.enonic.xp.market")
public class MarketServiceImpl
    implements MarketService
{
    private static final int connectionTimeout = 10_000;

    private static final int readTimeout = 10_000;

    private String marketUrl;

    @Activate
    public void activate( final MarketConfig config )
    {
        this.marketUrl = config.marketUrl();
    }

    @Override
    public MarketApplicationsJson get( final String version )
    {
        final Request request = MarketRequestFactory.create( this.marketUrl, version );

        try
        {
            final Response response = sendRequest( request );

            final int code = response.code();

            if ( code == HttpStatus.OK.value() )
            {
                try (final InputStream src = response.body().byteStream())
                {
                    return ObjectMapperHelper.create().
                        readValue( src, MarketApplicationsJson.class );
                }
            }
            else
            {
                throw new MarketException( "Cannot get applications from market [" + this.marketUrl + "], http status code " + code );
            }
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Illegal response from market [" + this.marketUrl + "]", e );
        }

    }

    private static Response sendRequest( final Request request )
        throws IOException
    {
        final OkHttpClient client = new OkHttpClient();
        client.setReadTimeout( readTimeout, TimeUnit.MILLISECONDS );
        client.setConnectTimeout( connectionTimeout, TimeUnit.MILLISECONDS );
        return client.newCall( request ).execute();
    }


}
