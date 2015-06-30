package com.enonic.xp.toolbox.repo;

import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import io.airlift.airline.Command;

@Command(name = "listSnapshots", description = "List snapshots...")
public final class ListSnapshotsCommand
    extends RepoCommand
{
    @Override
    protected void execute()
        throws Exception
    {
        final OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder().
            url( "http://localhost:8080/admin/rest/repo/list" ).
            header( "Authorization", Credentials.basic( "user", "password" ) ).
            build();

        final Response response = client.newCall( request ).execute();
        if ( !response.isSuccessful() )
        {
            throw new RuntimeException( "Could not execute command - " + response );
        }

        System.out.println( response.body().string() );
    }
}
