package com.enonic.xp.toolbox.repo;

import java.util.List;

import io.airlift.airline.Command;
import io.airlift.airline.Option;

@Command(name = "reindex", description = "Reindex...")
public final class ReindexCommand
    extends RepoCommand
{
    public static final String REINDEX_REST_PATH = "/admin/rest/repo/reindex";

    @Option(name = "-b", description = "A comma-separated list of branches to be reindexed.", required = true)
    public List<String> branches;

    @Option(name = "-r", description = "The name of the repository to reindex.", required = true)
    public String repository;

    @Option(name = "-i", description = "If flag -i given true, the indices will be deleted before recreated.", required = false)
    public boolean initialize = false;

    @Override
    protected void execute()
        throws Exception
    {
        final ReindexJsonRequest request = new ReindexJsonRequest().
            repositoryId( repository ).
            branches( branches ).
            initialize( initialize );
        final String jsonRequest = new RequestJsonSerializer().serialize( request );
        final String result = postRequest( REINDEX_REST_PATH, jsonRequest );

        System.out.println( result );
    }
}
