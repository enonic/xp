package com.enonic.xp.toolbox.repo;

import io.airlift.airline.Command;
import io.airlift.airline.Option;

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
        final ImportJsonRequest request = new ImportJsonRequest().
            targetRepoPath( targetRepoPath ).
            sourceDirectory( sourceDir ).
            importWithIds( importWithIds );
        final String jsonRequest = new RequestJsonSerializer().serialize( request );
        final String result = postRequest( IMPORT_REST_PATH, jsonRequest );

        System.out.println( result );
    }
}
