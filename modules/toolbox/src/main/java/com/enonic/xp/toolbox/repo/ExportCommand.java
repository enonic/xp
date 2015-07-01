package com.enonic.xp.toolbox.repo;

import io.airlift.airline.Command;
import io.airlift.airline.Option;

@Command(name = "export", description = "Export...")
public final class ExportCommand
    extends RepoCommand
{
    public static final String EXPORT_REST_PATH = "/admin/rest/export/export";

    @Option(name = "-t", description = "Target directory to save export.", required = true)
    public String targetDir;

    @Option(name = "-s", description = "Path of data to export. Format: <repo-name>:<branch-name>:<node-path>.", required = true)
    public String sourceRepoPath;

    @Option(name = "-i", description = "Flag that includes ids in data when exporting.", required = false)
    public boolean importWithIds = false;

    @Override
    protected void execute()
        throws Exception
    {
        final ExportJsonRequest request = new ExportJsonRequest().
            targetDirectory( targetDir ).
            sourceRepoPath( sourceRepoPath ).
            importWithIds( importWithIds );
        final String jsonRequest = new RequestJsonSerializer().serialize( request );
        final String result = postRequest( EXPORT_REST_PATH, jsonRequest );

        System.out.println( result );
    }
}
