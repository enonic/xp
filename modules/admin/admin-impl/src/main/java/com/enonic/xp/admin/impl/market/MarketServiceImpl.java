package com.enonic.xp.admin.impl.market;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.squareup.okhttp.Response;

import com.enonic.xp.admin.impl.rest.resource.application.json.MarketApplicationsJson;
import com.enonic.xp.json.ObjectMapperHelper;
import com.enonic.xp.market.MarketException;
import com.enonic.xp.web.HttpStatus;

@Component(immediate = true, configurationPid = "com.enonic.xp.market")
public class MarketServiceImpl
    implements MarketService
{
    private String marketUrl;
    private String marketProxy;

    private MarketDataProvider provider;

    @Activate
    public void activate( final MarketConfig config )
    {
        this.marketUrl = config.marketUrl();
        this.marketProxy = config.marketProxy();
    }

    @Override
    public MarketApplicationsJson get( final String version, final int from, final int count )
    {
        final Response response = this.provider.fetch( this.marketUrl, this.marketProxy, version, from, count );

        return parseResponse( response );
    }

    private MarketApplicationsJson parseResponse( final Response response )
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

    @Reference
    public void setProvider( final MarketDataProvider provider )
    {
        this.provider = provider;
    }
}
