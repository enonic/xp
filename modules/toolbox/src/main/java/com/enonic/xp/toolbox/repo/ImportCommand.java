package com.enonic.xp.toolbox.repo;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.airlift.airline.Command;
import io.airlift.airline.Option;

import com.enonic.xp.toolbox.util.JsonHelper;

@Command(name = "import", description = "Import nodes from an export into a repository branch.")
public final class ImportCommand
    extends RepoCommand
{
    public static final String IMPORT_REST_PATH = "/admin/rest/export/import";

    @Option(name = "-t", description = "Target path for import. Format: <repo-name>:<branch-name>:<node-path>. e.g 'cms-repo:draft:/'", required = true)
    public String targetRepoPath;

    @Option(name = "-s", description = "Path to exported files.", required = true)
    public String sourceDir;

    @Option(name = "-i", description = "Flag that includes ids.", required = false)
    public boolean importWithIds = false;

    @Override
    protected void execute()
        throws Exception
    {
        final String result = postRequest( IMPORT_REST_PATH, createJsonRequest() );
        System.out.println( result );
    }

    private ObjectNode createJsonRequest()
    {
        final ObjectNode json = JsonHelper.newObjectNode();
        json.put( "sourceDirectory", this.sourceDir );
        json.put( "targetRepoPath", this.targetRepoPath );
        json.put( "importWithIds", this.importWithIds );
        return json;
    }
}
