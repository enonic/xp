package com.enonic.xp.toolbox.repo;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.airlift.airline.Arguments;
import io.airlift.airline.Command;

import com.enonic.xp.toolbox.util.JsonHelper;

@Command(name = "set-replicas", description = "Set the number of replicas.")
public final class SetReplicasCommand
    extends RepoCommand
{
    public static final String UPDATE_INDEX_SETTINGS_REST_PATH = "/admin/rest/repo/updateIndexSettings";

    @Arguments(description = "Number of replicas", required = true)
    public Integer numberOfReplicas;

    @Override
    protected void execute()
        throws Exception
    {
        final String result = postRequest( UPDATE_INDEX_SETTINGS_REST_PATH, createJsonRequest() );
        System.out.println( result );
    }

    private ObjectNode createJsonRequest()
    {
        final ObjectNode settingsIndexObjectNode = JsonHelper.newObjectNode();
        settingsIndexObjectNode.put( "number_of_replicas", numberOfReplicas );

        final ObjectNode settingsObjectNode = JsonHelper.newObjectNode();
        settingsObjectNode.set( "index", settingsIndexObjectNode );

        final ObjectNode json = JsonHelper.newObjectNode();
        json.set( "settings", settingsObjectNode );
        return json;
    }
}
