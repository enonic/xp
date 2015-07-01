package com.enonic.xp.toolbox.repo;

import io.airlift.airline.Command;
import io.airlift.airline.Option;

@Command(name = "deleteSnapshots", description = "Delete snapshots")
public final class DeleteSnapshotsCommand
    extends RepoCommand
{
    public static final String DELETE_SNAPSHOTS_REST_PATH = "/admin/rest/repo/delete";

    @Option(name = "-b", description = "Delete snapshots before this timestamp.", required = true)
    public String before;

    @Override
    protected void execute()
        throws Exception
    {
        final DeleteSnapshotsJsonRequest request = new DeleteSnapshotsJsonRequest().beforeTimestamp( before );
        final String jsonRequest = new RequestJsonSerializer().serialize( request );
        final String result = postRequest( DELETE_SNAPSHOTS_REST_PATH, jsonRequest );

        System.out.println( result );
    }
}
