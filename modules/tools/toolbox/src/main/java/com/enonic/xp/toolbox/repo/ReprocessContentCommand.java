package com.enonic.xp.toolbox.repo;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.airlift.airline.Command;
import io.airlift.airline.Option;

import com.enonic.xp.toolbox.util.JsonHelper;

@Command(name = "reprocess", description = "Reprocesses content in the repository.")
public final class ReprocessContentCommand
    extends RepoCommand
{
    public static final String REPROCESS_CONTENT_REST_PATH = "/content/reprocess";

    @Option(name = "-s", description = "Target content path to be reprocessed. Format: <branch-name>:<content-path>. e.g 'draft:/'", required = true)
    public String sourceBranchPath;

    @Option(name = "--skip-children", description = "Flag to skip processing of content children.", required = false)
    public boolean skipChildren = false;

    @Override
    protected void execute()
        throws Exception
    {
        final String result = postRequest( REPROCESS_CONTENT_REST_PATH, createJsonRequest() );
        System.out.println( result );
    }

    private ObjectNode createJsonRequest()
    {
        final ObjectNode json = JsonHelper.newObjectNode();
        json.put( "sourceBranchPath", this.sourceBranchPath );
        json.put( "skipChildren", this.skipChildren );
        return json;
    }
}
