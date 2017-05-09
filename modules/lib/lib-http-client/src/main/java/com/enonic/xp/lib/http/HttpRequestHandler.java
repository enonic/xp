package com.enonic.xp.lib.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.io.ByteSource;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.http.HttpMethod;

import static org.apache.commons.lang.StringUtils.isBlank;

@SuppressWarnings("WeakerAccess")
public final class HttpRequestHandler
{
    private final static int DEFAULT_PROXY_PORT = 8080;

    static long MAX_IN_MEMORY_BODY_STREAM_BYTES = 10_000_000;

    private String url;

    private Map<String, Object> params;

    private String method = "GET";

    private Map<String, String> headers;

    private int connectionTimeout = 10_000;

    private int readTimeout = 10_000;

    private String contentType;

    private String bodyString;

    private ByteSource bodyStream;

    private List<Map<String, Object>> multipart;

    private String proxyHost;

    private Integer proxyPort;

    private String proxyUser;

    private String proxyPassword;

    private String authUser;

    private String authPassword;

    private final static OkHttpClient CLIENT = new OkHttpClient();

    @SuppressWarnings("unused")
    public ResponseMapper request()
        throws IOException
    {
        final Response response = sendRequest( getRequest() );
        return new ResponseMapper( response );
    }

    private Response sendRequest( final Request request )
        throws IOException
    {
        final OkHttpClient.Builder clientBuilder = CLIENT.newBuilder();
        clientBuilder.readTimeout( this.readTimeout, TimeUnit.MILLISECONDS );
        clientBuilder.connectTimeout( this.connectionTimeout, TimeUnit.MILLISECONDS );
        setupProxy( clientBuilder );
        setupAuthentication( clientBuilder );

        return clientBuilder.build().newCall( request ).execute();
    }

    private Request getRequest()
        throws IOException
    {
        final Request.Builder request = new Request.Builder();
        request.url( this.url );

        RequestBody requestBody = null;
        if ( this.params != null && !this.params.isEmpty() )
        {
            final FormBody.Builder formBody = new FormBody.Builder();
            addParams( formBody, this.params );
            requestBody = formBody.build();
        }
        else if ( this.bodyString != null && !this.bodyString.isEmpty() )
        {
            final MediaType mediaType = this.contentType != null ? MediaType.parse( this.contentType ) : null;
            requestBody = RequestBody.create( mediaType, this.bodyString );
        }
        else if ( this.bodyStream != null )
        {
            final MediaType mediaType = this.contentType != null ? MediaType.parse( this.contentType ) : null;
            requestBody = RequestBody.create( mediaType, this.bodyStream.read() );
        }
        else if ( this.multipart != null )
        {
            requestBody = getMultipartBody();
        }

        if ( "GET".equals( this.method ) )
        {
            HttpUrl url = HttpUrl.parse( this.url );
            if ( this.params != null )
            {
                url = addParams( url, this.params );
            }
            request.url( url );
            if ( this.contentType != null )
            {
                request.header( "Content-Type", this.contentType );
            }
            request.get();
        }
        else
        {
            if ( requestBody == null && HttpMethod.requiresRequestBody( this.method ) )
            {
                final MediaType mediaType = this.contentType != null ? MediaType.parse( this.contentType ) : null;
                requestBody = RequestBody.create( mediaType, "" );
            }
            request.method( this.method, requestBody );
        }

        addHeaders( request, this.headers );
        addAuthHeaders( request );
        return request.build();
    }

    private RequestBody getMultipartBody()
        throws IOException
    {
        final MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType( MultipartBody.FORM );

        for ( Map<String, Object> multipartItem : this.multipart )
        {
            final String name = getValue( multipartItem, "name" );
            final String fileName = getValue( multipartItem, "fileName" );
            final String contentType = getValue( multipartItem, "contentType" );
            final Object value = multipartItem.get( "value" );
            if ( isBlank( name ) || value == null )
            {
                continue;
            }

            if ( value instanceof ByteSource )
            {
                final ByteSource stream = (ByteSource) value;
                final String ct = contentType == null ? "application/octet-stream" : contentType;
                final MediaType partMediaType = MediaType.parse( ct );
                final byte[] content = stream.read();
                final RequestBody body = RequestBody.create( partMediaType, content );
                multipartBuilder.addFormDataPart( name, fileName, body );
            }
            else
            {
                multipartBuilder.addFormDataPart( name, value.toString() );
            }
        }
        return multipartBuilder.build();
    }

    private String getValue( final Map<String, Object> object, final String key )
    {
        final Object value = object.get( key );
        return value == null ? null : value.toString();
    }

    private HttpUrl addParams( final HttpUrl url, final Map<String, Object> params )
    {
        HttpUrl.Builder urlBuilder = url.newBuilder();
        params.entrySet().stream().
            filter( header -> header.getValue() != null ).
            forEach( header -> urlBuilder.addEncodedQueryParameter( header.getKey(), header.getValue().toString() ) );
        return urlBuilder.build();
    }

    private void addParams( final FormBody.Builder formBody, final Map<String, Object> params )
    {
        params.entrySet().stream().
            filter( header -> header.getValue() != null ).
            forEach( header -> formBody.add( header.getKey(), header.getValue().toString() ) );
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

    private void setupProxy( final OkHttpClient.Builder client )
    {
        if ( proxyHost == null || proxyHost.trim().isEmpty() )
        {
            return;
        }
        int proxyPort = this.proxyPort == null ? DEFAULT_PROXY_PORT : this.proxyPort;
        client.proxy( new Proxy( Proxy.Type.HTTP, new InetSocketAddress( proxyHost, proxyPort ) ) );
    }

    private void setupAuthentication( final OkHttpClient.Builder client )
    {
        final String authUser = this.authUser;
        final String authPassword = this.authPassword;
        final String proxyUser = this.proxyUser;
        final String proxyPassword = this.proxyPassword;

        if ( ( proxyUser == null || proxyUser.isEmpty() ) && ( authUser == null || authUser.isEmpty() ) )
        {
            return;
        }

        Authenticator authenticator = ( route, response ) -> {
            if ( authUser == null || authUser.trim().isEmpty() )
            {
                return null;
            }
            String credential = Credentials.basic( authUser, authPassword );
            if ( credential.equals( response.request().header( "Authorization" ) ) )
            {
                return null; // If we already failed with these credentials, don't retry
            }
            return response.request().newBuilder().header( "Authorization", credential ).build();
        };

        Authenticator proxyAuthenticator = ( route, response ) -> {
            if ( proxyUser == null || proxyUser.trim().isEmpty() )
            {
                return null;
            }
            String credential = Credentials.basic( proxyUser, proxyPassword );
            return response.request().newBuilder().header( "Proxy-Authorization", credential ).build();
        };

        client.authenticator( authenticator );
        client.proxyAuthenticator( proxyAuthenticator );
    }


    private void addAuthHeaders( final Request.Builder request )
    {
        if ( authUser != null && authPassword != null )
        {
            String credential = Credentials.basic( authUser, authPassword );
            request.header( "Authorization", credential ).build();
        }
    }

    @SuppressWarnings("unused")
    public void setContentType( final String contentType )
    {
        this.contentType = contentType;
    }

    @SuppressWarnings("unused")
    public void setBody( final Object value )
    {
        this.bodyStream = null;
        this.bodyString = null;
        if ( value == null )
        {
            return;
        }
        if ( value instanceof ByteSource )
        {
            this.bodyStream = (ByteSource) value;
        }
        else
        {
            this.bodyString = value.toString();
        }
    }

    @SuppressWarnings("unused")
    public void setHeaders( final Map<String, String> headers )
    {
        this.headers = headers;
    }

    @SuppressWarnings("unused")
    public void setUrl( final String value )
    {
        this.url = value;
    }

    @SuppressWarnings("unused")
    public void setParams( final Map<String, Object> params )
    {
        this.params = params;
    }

    @SuppressWarnings("unused")
    public void setMethod( final String value )
    {
        if ( value != null )
        {
            this.method = value.trim().toUpperCase();
        }
    }

    @SuppressWarnings("unused")
    public void setConnectionTimeout( final Integer value )
    {
        if ( value != null )
        {
            this.connectionTimeout = value;
        }
    }

    @SuppressWarnings("unused")
    public void setReadTimeout( final Integer value )
    {
        if ( value != null )
        {
            this.readTimeout = value;
        }
    }

    @SuppressWarnings("unused")
    public void setMultipart( final List<Map<String, Object>> multipart )
    {
        this.multipart = multipart;
    }

    @SuppressWarnings("unused")
    public void setProxyHost( final String proxyHost )
    {
        this.proxyHost = proxyHost;
    }

    @SuppressWarnings("unused")
    public void setProxyPort( final Integer proxyPort )
    {
        this.proxyPort = proxyPort;
    }

    @SuppressWarnings("unused")
    public void setProxyUser( final String proxyUser )
    {
        this.proxyUser = proxyUser;
    }

    @SuppressWarnings("unused")
    public void setProxyPassword( final String proxyPassword )
    {
        this.proxyPassword = proxyPassword;
    }

    @SuppressWarnings("unused")
    public void setAuthUser( final String authUser )
    {
        this.authUser = authUser;
    }

    @SuppressWarnings("unused")
    public void setAuthPassword( final String authPassword )
    {
        this.authPassword = authPassword;
    }
}
