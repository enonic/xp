package com.enonic.xp.toolbox.repo;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.airlift.airline.Command;
import io.airlift.airline.Option;

import com.enonic.xp.toolbox.util.JsonHelper;

@Command(name = "export", description = "Export data for a specified path.")
public final class ExportCommand
    extends RepoCommand
{
    private static final String EXPORT_REST_PATH = "/api/repo/export";

    @SuppressWarnings("WeakerAccess")
    @Option(name = "-t", description = "Target name to save export.", required = true)
    public String exportName;

    @SuppressWarnings("WeakerAccess")
    @Option(name = "-s", description = "Path of data to export. Format: <repo-name>:<branch-name>:<node-path>.", required = true)
    public String sourceRepoPath;

    @SuppressWarnings("WeakerAccess")
    @Option(name = "--skipids", description = "Flag that skips ids in data when exporting.", required = false)
    public boolean skipids = false;


    @Override
    protected void execute()
        throws Exception
    {
        final String result = postRequest( EXPORT_REST_PATH, createJsonRequest() );
        System.out.println( result );
    }

    private ObjectNode createJsonRequest()
    {
        final ObjectNode json = JsonHelper.newObjectNode();
        json.put( "sourceRepoPath", this.sourceRepoPath );
        json.put( "exportName", this.exportName );
        json.put( "exportWithIds", !this.skipids );
        return json;
    }
}
