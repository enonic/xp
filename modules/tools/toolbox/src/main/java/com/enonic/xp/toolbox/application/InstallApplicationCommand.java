package com.enonic.xp.toolbox.application;

import java.io.File;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import io.airlift.airline.Command;
import io.airlift.airline.Option;

import com.enonic.xp.toolbox.repo.RepoCommand;
import com.enonic.xp.toolbox.util.JsonHelper;

@Command(name = "install-app", description = "Install an application from URL or file")
public class InstallApplicationCommand
    extends RepoCommand
{
    public static final String INSTALL_APP_URL_REST_PATH = "/api/app/installUrl";

    public static final String INSTALL_APP_FILE_REST_PATH = "/api/app/install";

    @Option(name = "-u", description = "The URL of the application", required = false)
    public String url;

    @Option(name = "-f", description = "Application file", required = false)
    public File file;


    @Override
    protected void execute()
        throws Exception
    {
        if ( Strings.isNullOrEmpty( url ) && file == null )
        {
            throw new IllegalArgumentException( "Must provide either url (-u) or file (-f) option" );
        }

        final String result;

        if ( file != null )
        {
            result = installFromFile( file );
        }
        else
        {
            result = installFromUrl();
        }

        System.out.println( result );
    }

    private String installFromUrl()
        throws Exception
    {
        return postRequest( INSTALL_APP_URL_REST_PATH, createJsonRequest() );
    }

    private String installFromFile( final File file )
        throws Exception
    {
        if ( !file.exists() )
        {
            throw new IllegalArgumentException( "File " + file.getPath() + " does not exist" );
        }

        final Request request = createMultipartRequest( file );

        return executeRequest( request );
    }

    private Request createMultipartRequest( final File file )
    {
        final String url = scheme + "://" + host + ":" + port + INSTALL_APP_FILE_REST_PATH;

        final RequestBody body = RequestBody.create( MediaType.parse( "multipart/form-data" ), file );

        final MultipartBuilder multipartBuilder = new MultipartBuilder().
            addFormDataPart( "file", file.getName(), body ).
            type( MultipartBuilder.FORM );

        return new Request.Builder().
            url( url ).
            post( multipartBuilder.build() ).
            header( "Authorization", authCredentials() ).
            build();
    }

    private ObjectNode createJsonRequest()
    {
        final ObjectNode json = JsonHelper.newObjectNode();
        if ( url != null )
        {
            json.put( "URL", this.url );
        }
        return json;
    }


}
