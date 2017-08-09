package com.enonic.xp.toolbox.repo;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import io.airlift.airline.Option;

import com.enonic.xp.toolbox.util.JsonHelper;

@Command(name = "set-read-only", description = "Sets the cluster to read-only mode")
public final class SetReadOnlyCommand
    extends RepoCommand
{
    public static final String REST_PATH = "/api/repo/index/setReadOnlyMode";

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
        final ObjectNode json = JsonHelper.newObjectNode();
        json.put( "readOnly", readOnly );
        json.put( "repositoryId", repositoryId );
        return json;
    }
}
