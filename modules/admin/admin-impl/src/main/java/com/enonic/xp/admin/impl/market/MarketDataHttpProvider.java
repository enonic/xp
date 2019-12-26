package com.enonic.xp.admin.impl.market;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.io.CharStreams;

import com.enonic.xp.admin.impl.rest.resource.application.json.MarketApplicationsJson;
import com.enonic.xp.json.ObjectMapperHelper;
import com.enonic.xp.market.MarketException;
import com.enonic.xp.web.HttpStatus;

@Component(immediate = true, configurationPid = "com.enonic.xp.market")
public class MarketDataHttpProvider
    implements MarketDataProvider
{
    private static final Duration CONNECTION_TIMEOUT = Duration.ofSeconds( 10 );

    private static final ObjectReader JSON_READER = ObjectMapperHelper.create().readerFor( MarketApplicationsJson.class );

    private String marketUrl;

    @Activate
    public void activate( final MarketConfig config )
    {
        this.marketUrl = config.marketUrl();
    }

    @Override
    public MarketApplicationsJson search( List<String> ids, String version, int start, int count )
    {
        final HttpRequest request = MarketRequestFactory.create( marketUrl, ids, version, start, count );

        return doRequest( request );
    }

    private MarketApplicationsJson doRequest( HttpRequest request )
    {
        final HttpClient client = HttpClient.newBuilder().
            connectTimeout( CONNECTION_TIMEOUT ).
            build();

        final HttpResponse<InputStream> response;
        try
        {
            response = client.send( request, HttpResponse.BodyHandlers.ofInputStream() );
        }
        catch ( IOException | InterruptedException e )
        {
            throw new MarketException( "Cannot connect to market", e );
        }
        return parseResponse( response );
    }

    private MarketApplicationsJson parseResponse( final HttpResponse<InputStream> response )
    {
        final int code = response.statusCode();

        if ( code == HttpStatus.OK.value() )
        {
            try
            {
                return parseResponseBody( response );
            }
            catch ( IOException e )
            {
                throw new MarketException( "Failed to get response from market", e );
            }
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

    private MarketApplicationsJson throwExceptionAttachBody( final HttpResponse<InputStream> response, final int code )
    {
        try (final InputStream bodyStream = response.body())
        {
            final String body = CharStreams.toString( new InputStreamReader( bodyStream, StandardCharsets.UTF_8 ) );

            throw new MarketException( "Cannot get applications from marked, server response : [body = " + body + "]", code );
        }
        catch ( IOException e )
        {
            throw new MarketException( "Cannot get applications from marked", code );
        }
    }

    private MarketApplicationsJson parseResponseBody( final HttpResponse<InputStream> response )
        throws IOException
    {
        final List<String> contentEncoding = response.headers().allValues( "Content-Encoding" );

        if ( contentEncoding.isEmpty() )
        {
            try (final InputStream src = response.body())
            {
                return JSON_READER.readValue( src );
            }
        }
        else if ( contentEncoding.equals( List.of( "gzip" ) ) )
        {
            try (final InputStream body = response.body(); final InputStream is = new GZIPInputStream( body ))
            {
                return JSON_READER.readValue( is );
            }
        }
        else
        {
            throw new IOException( "Unsupported Content-Encoding " + contentEncoding );
        }
    }
}
