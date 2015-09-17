package com.enonic.xp.toolbox.repo;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.airlift.airline.Command;
import io.airlift.airline.Option;

import com.enonic.xp.toolbox.util.JsonHelper;

@Command(name = "restore", description = "Restores a snapshot of a previous state of the repository.")
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
        final String result = postRequest( RESTORE_SNAPSHOT_REST_PATH, createJsonRequest() );
        System.out.println( result );
    }

    private ObjectNode createJsonRequest()
    {
        final ObjectNode json = JsonHelper.newObjectNode();
        json.put( "repository", this.repository );
        json.put( "snapshotName", this.snapshotName );
        return json;
    }
}
