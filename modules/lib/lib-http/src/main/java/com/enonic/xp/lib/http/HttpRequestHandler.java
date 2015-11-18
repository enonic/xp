package com.enonic.xp.lib.http;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public final class HttpRequestHandler
{
    private String url;

    private Map<String, Object> params;

    private String method = "GET";

    private Map<String, String> headers;

    private int connectionTimeout = 10_000;

    private int readTimeout = 10_000;

    private String contentType;

    private String body;

    public void setContentType( final String contentType )
    {
        this.contentType = contentType;
    }

    public void setBody( final String value )
    {
        this.body = value;
    }

    public void setHeaders( final Map<String, String> headers )
    {
        this.headers = headers;
    }

    public void setUrl( final String value )
    {
        this.url = value;
    }

    public void setParams( final Map<String, Object> params )
    {
        this.params = params;
    }

    public void setMethod( final String value )
    {
        if ( value != null )
        {
            this.method = value.trim().toUpperCase();
        }
    }

    public void setConnectionTimeout( final Integer value )
    {
        if ( value != null )
        {
            this.connectionTimeout = value;
        }
    }

    public void setReadTimeout( final Integer value )
    {
        if ( value != null )
        {
            this.readTimeout = value;
        }
    }

    public ResponseMapper request()
        throws IOException
    {
        final Response response = sendRequest( getRequest() );
        return new ResponseMapper( response );
    }

    private Response sendRequest( final Request request )
        throws IOException
    {
        final OkHttpClient client = new OkHttpClient();
        client.setReadTimeout( this.readTimeout, TimeUnit.MILLISECONDS );
        client.setConnectTimeout( this.connectionTimeout, TimeUnit.MILLISECONDS );
        return client.newCall( request ).execute();
    }

    private Request getRequest()
    {
        final Request.Builder request = new Request.Builder();
        request.url( this.url );

        RequestBody requestBody = null;
        if ( this.params != null && !this.params.isEmpty() )
        {
            final FormEncodingBuilder formBody = new FormEncodingBuilder();
            addParams( formBody, this.params );
            requestBody = formBody.build();
        }
        else if ( this.body != null )
        {
            final MediaType mediaType = this.contentType != null ? MediaType.parse( this.contentType ) : null;
            requestBody = RequestBody.create( mediaType, this.body );
        }

        if ( "GET".equals( this.method ) && this.params != null )
        {
            HttpUrl url = HttpUrl.parse( this.url );
            url = addParams( url, this.params );
            request.url( url );
            request.get();
        }
        else
        {
            request.method( this.method, requestBody );
        }

        addHeaders( request, this.headers );
        return request.build();
    }

    private HttpUrl addParams( final HttpUrl url, final Map<String, Object> params )
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

    private void addParams( final FormEncodingBuilder formBody, final Map<String, Object> params )
    {
        for ( Map.Entry<String, Object> header : params.entrySet() )
        {
            if ( header.getValue() != null )
            {
                formBody.add( header.getKey(), header.getValue().toString() );
            }
        }
    }

    private void addHeaders( final Request.Builder request, final Map<String, String> headers )
    {
        if ( headers != null )
        {
            for ( Map.Entry<String, String> header : headers.entrySet() )
            {
                request.header( header.getKey(), header.getValue() );
            }
        }
    }

}
