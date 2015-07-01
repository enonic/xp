package com.enonic.xp.toolbox.repo;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.airlift.airline.Command;
import io.airlift.airline.Option;

import com.enonic.xp.toolbox.util.JsonHelper;

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
        final String result = postRequest( SNAPSHOT_REST_PATH, createJsonRequest() );
        System.out.println( result );
    }

    private ObjectNode createJsonRequest()
    {
        final ObjectNode json = JsonHelper.newObjectNode();
        json.put( "repositoryId", this.repository );
        return json;
    }
}
