package com.enonic.xp.admin.impl.market;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import com.enonic.xp.admin.impl.rest.resource.application.json.MarketApplicationsJson;
import com.enonic.xp.json.ObjectMapperHelper;
import com.enonic.xp.market.MarketException;
import com.enonic.xp.web.HttpStatus;

@Component(immediate = true, configurationPid = "com.enonic.xp.market")
public class MarketDataHttpProvider
    implements MarketDataProvider
{
    private static final int connectionTimeout = 10_000;

    private static final int readTimeout = 10_000;

    private String marketUrl;

    @Activate
    public void activate( final MarketConfig config )
    {
        this.marketUrl = config.marketUrl();
    }

    public MarketApplicationsJson search( List<String> ids, String version, int start, int count )
    {
        final Request request = MarketRequestFactory.create( marketUrl, ids, version, start, count );

        return doRequest( request );
    }

    private MarketApplicationsJson doRequest( Request request )
    {

        final OkHttpClient client = new OkHttpClient();
        client.setReadTimeout( readTimeout, TimeUnit.MILLISECONDS );
        client.setConnectTimeout( connectionTimeout, TimeUnit.MILLISECONDS );

        try
        {
            final Response response = client.newCall( request ).execute();
            return parseResponse( response );
        }
        catch ( IOException e )
        {
            throw new MarketException( "Cannot connect to market", e );
        }
    }

    protected MarketApplicationsJson parseResponse( final Response response )
    {
        final int code = response.code();

        if ( code == HttpStatus.OK.value() )
        {
            return parseJson( response );
        }
        else if ( code == HttpStatus.INTERNAL_SERVER_ERROR.value() )
        {
            return throwExceptionAttachBody( response, code );
        }
        else
        {
            throw new MarketException( "Cannot get applications from market", code );
        }
    }

    private MarketApplicationsJson throwExceptionAttachBody( final Response response, final int code )
    {
        try (final InputStream bodyStream = response.body().byteStream())
        {
            final String body = CharStreams.toString( new InputStreamReader( bodyStream, Charsets.UTF_8 ) );

            throw new MarketException( "Cannot get applications from marked, server response : [body = " + body + "]", code );
        }
        catch ( IOException e )
        {
            throw new MarketException( "Cannot get applications from marked", code );
        }
    }

    private MarketApplicationsJson parseJson( final Response response )
    {
        try (final InputStream src = response.body().byteStream())
        {
            return ObjectMapperHelper.create().
                readValue( src, MarketApplicationsJson.class );
        }
        catch ( JsonParseException | JsonMappingException e )
        {
            throw new MarketException( "Failed to parse response from market", e );
        }
        catch ( IOException e )
        {
            throw new MarketException( "Failed to get response from market", e );
        }
    }

}
