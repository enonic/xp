package com.enonic.xp.toolbox.repo;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.airlift.airline.Command;
import io.airlift.airline.Option;

import com.enonic.xp.toolbox.util.JsonHelper;

@Command(name = "dump", description = "Export data from every repository.")
public final class DumpCommand
    extends RepoCommand
{
    private static final String SYSTEM_DUMP_REST_PATH = "/api/system/dump";

    @SuppressWarnings("WeakerAccess")
    @Option(name = "-t", description = "Dump name.", required = true)
    public String target;

    @SuppressWarnings("WeakerAccess")
    @Option(name = "--skip-versions", description = "Dont dump version-history, only current versions included")
    public boolean skipVersions = false;

    @SuppressWarnings("WeakerAccess")
    @Option(name = "--max-version-age", description = "Max age of versions to include, in days, in addition to current version")
    public Integer maxAge;

    @SuppressWarnings("WeakerAccess")
    @Option(name = "--max-versions", description = "Max number of versions to dump in addition to current version")
    public Integer maxVersions;

    @Override
    protected void execute()
        throws Exception
    {
        final String result = postRequest( SYSTEM_DUMP_REST_PATH, createJsonRequest() );
        System.out.println( result );
    }

    private ObjectNode createJsonRequest()
    {
        final ObjectNode json = JsonHelper.newObjectNode();
        json.put( "name", this.target );
        json.put( "includeVersions", !this.skipVersions );
        if ( this.maxAge != null )
        {
            json.put( "maxAge", this.maxAge );
        }
        if ( this.maxVersions != null )
        {
            json.put( "maxVersions", this.maxVersions );
        }
        return json;
    }
}
