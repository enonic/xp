package com.enonic.xp.toolbox.repo;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;

import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import io.airlift.airline.Option;

import com.enonic.xp.toolbox.util.JsonHelper;

@Command(name = "set-read-only", description = "Toggle read-only mode for server or single repository")
public final class SetReadOnlyCommand
    extends RepoCommand
{
    public static final String REST_PATH = "/api/repo/index/updateSettings";

    @Arguments(description = "Read only mode enabled", required = true)
    public boolean readOnly;

    @Option(name = "-r", description = "Single repository to toggle read-only mode for", required = false)
    public String repositoryId;

    @Override
    protected void execute()
        throws Exception
    {
        final String result = postRequest( REST_PATH, createJsonRequest() );
        System.out.println( result );
    }

    private ObjectNode createJsonRequest()
    {
        final ObjectNode settingsIndexObjectNode = JsonHelper.newObjectNode();
        settingsIndexObjectNode.put( "blocks.write", readOnly );

        final ObjectNode settingsObjectNode = JsonHelper.newObjectNode();
        settingsObjectNode.set( "index", settingsIndexObjectNode );

        final ObjectNode json = JsonHelper.newObjectNode();
        json.set( "settings", settingsObjectNode );

        if ( !Strings.isNullOrEmpty( repositoryId ) )
        {
            json.put( "repositoryId", repositoryId );
        }

        json.put( "requireClosedIndex", true );

        return json;
    }
}
