package com.enonic.xp.toolbox.repo;

import java.util.List;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.airlift.airline.Command;
import io.airlift.airline.Option;

import com.enonic.xp.toolbox.util.JsonHelper;

@Command(name = "reindex", description = "Reindex content in search indices for the given repository and branches.")
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
        final String result = postRequest( REINDEX_REST_PATH, createJsonRequest() );
        System.out.println( result );
    }

    private ObjectNode createJsonRequest()
    {
        final ObjectNode json = JsonHelper.newObjectNode();
        json.put( "repositoryId", this.repository );
        json.put( "initialize", this.initialize );

        final ArrayNode branchesNode = json.putArray( "branches" );
        this.branches.forEach( branchesNode::add );

        return json;
    }
}
