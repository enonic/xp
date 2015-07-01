package com.enonic.xp.toolbox.repo;

import io.airlift.airline.Command;
import io.airlift.airline.Option;

@Command(name = "snapshot", description = "Stores a snapshot of the current state of the repository.")
public final class SnapshotCommand
    extends RepoCommand
{
    public static final String SNAPSHOT_REST_PATH = "/admin/rest/repo/snapshot";

    @Option(name = "-r", description = "the name of the repository to snapshot.", required = true)
    public String repository;

    @Override
    protected void execute()
        throws Exception
    {
        final SnapshotJsonRequest request = new SnapshotJsonRequest().repositoryId( repository );
        final String jsonRequest = new RequestJsonSerializer().serialize( request );
        final String result = postRequest( SNAPSHOT_REST_PATH, jsonRequest );

        System.out.println( result );
    }
}
