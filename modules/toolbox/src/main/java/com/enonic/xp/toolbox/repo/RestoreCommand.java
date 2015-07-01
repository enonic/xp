package com.enonic.xp.toolbox.repo;

import io.airlift.airline.Command;
import io.airlift.airline.Option;

@Command(name = "restore", description = "Stores a snapshot of the current state of the repository.")
public final class RestoreCommand
    extends RepoCommand
{
    public static final String RESTORE_SNAPSHOT_REST_PATH = "/admin/rest/repo/restore";

    @Option(name = "-r", description = "The name of the repository to restore.", required = true)
    public String repository;

    @Option(name = "-s", description = "The name of the snapshot to restore.", required = true)
    public String snapshotName;

    @Override
    protected void execute()
        throws Exception
    {
        final RestoreSnapshotJsonRequest request = new RestoreSnapshotJsonRequest().
            repositoryId( repository ).
            snapshotName( snapshotName );
        final String jsonRequest = new RequestJsonSerializer().serialize( request );
        final String result = postRequest( RESTORE_SNAPSHOT_REST_PATH, jsonRequest );

        System.out.println( result );
    }
}
