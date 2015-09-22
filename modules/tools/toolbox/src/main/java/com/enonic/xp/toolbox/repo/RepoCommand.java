package com.enonic.xp.toolbox.repo;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import io.airlift.airline.Option;

import com.enonic.xp.toolbox.ResponseException;
import com.enonic.xp.toolbox.ToolCommand;
import com.enonic.xp.toolbox.util.JsonHelper;

public abstract class RepoCommand
    extends ToolCommand
{
    private static final MediaType JSON = MediaType.parse( "application/json; charset=utf-8" );

    @Option(name = "-a", description = "Authentication token for basic authentication (user:password).", required = true)
    public String auth;

    @Option(name = "-h", description = "Host name for server (default is localhost).")
    public String host = "localhost";

    @Option(name = "-p", description = "Port number for server (default is 8080).")
    public int port = 8080;

    protected String postRequest( final String urlPath, final JsonNode json )
        throws Exception
    {
        return postRequest( urlPath, JsonHelper.serialize( json ) );
    }

    protected String postRequest( final String urlPath, final String json )
        throws Exception
    {
        final RequestBody body = RequestBody.create( JSON, json );

        final String url = "http://" + host + ":" + port + urlPath;
        final Request request = new Request.Builder().
            url( url ).
            post( body ).
            header( "Authorization", authCredentials() ).
            build();

        return executeRequest( request );
    }

    protected String getRequest( final String urlPath )
        throws Exception
    {
        final String url = "http://" + host + ":" + port + urlPath;
        final Request request = new Request.Builder().
            url( url ).
            get().
            header( "Authorization", authCredentials() ).
            build();

        return executeRequest( request );
    }

    private String executeRequest( final Request request )
        throws Exception
    {
        final OkHttpClient client = new OkHttpClient();
        final Response response = client.newCall( request ).execute();
        if ( !response.isSuccessful() )
        {
            final String responseBody = response.body().string();
            final String prettified = JsonHelper.prettifyJson( responseBody );
            throw new ResponseException( prettified, response.code() );
        }

        final String responseBody = response.body().string();
        return JsonHelper.prettifyJson( responseBody );
    }

    private String authCredentials()
    {
        final String[] authParts = auth.split( ":" );
        final String userName = authParts.length > 0 ? authParts[0] : "";
        final String password = authParts.length > 1 ? authParts[1] : "";
        return Credentials.basic( userName, password );
    }
}
