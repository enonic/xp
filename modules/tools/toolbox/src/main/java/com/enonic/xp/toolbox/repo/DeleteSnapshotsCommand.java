package com.enonic.xp.toolbox.repo;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.airlift.airline.Command;
import io.airlift.airline.Option;

import com.enonic.xp.toolbox.util.JsonHelper;

@Command(name = "delete-snapshots", description = "Deletes snapshots, either before a given timestamp or by name.")
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
        final String result = postRequest( DELETE_SNAPSHOTS_REST_PATH, createJsonRequest() );
        System.out.println( result );
    }

    private ObjectNode createJsonRequest()
    {
        final ObjectNode json = JsonHelper.newObjectNode();
        json.put( "before", this.before );
        return json;
    }
}
