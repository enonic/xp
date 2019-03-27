package com.enonic.xp.toolbox.repo;

import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.airlift.airline.Command;
import io.airlift.airline.Option;

import com.enonic.xp.toolbox.util.JsonHelper;

@Command(name = "import", description = "Import data from a named export.")
public final class ImportCommand
    extends RepoCommand
{
    public static final String IMPORT_REST_PATH = "/repo/import";

    @Option(name = "-t", description = "Target path for import. Format: <repo-name>:<branch-name>:<node-path>. e.g 'com.enonic.cms.default:draft:/'", required = true)
    public String targetRepoPath;

    @Option(name = "-s", description = "A named export to import.", required = true)
    public String exportName;

    @Option(name = "--skipids", description = "Flag that skips ids.", required = false)
    public boolean skipids = false;

    @Option(name = "--skip-permissions", description = "Flag that skips permissions.", required = false)
    public boolean skipPermissions = false;

    @Option(name = "-xslSource", description = "Path to xsl file (relative to <XP_HOME>/data/export) for applying transformations to node.xml before importing.", required = false)
    public String xslSource;

    @Option(name = "-xslParam", description = "Parameter to pass to the XSL transformations before importing nodes. Format: <parameter-name>=<parameter-value> . e.g. 'applicationId=com.enonic.myapp'", required = false)
    public List<String> xslParam;

    @Override
    protected void execute()
        throws Exception
    {
        final String result = postRequest( IMPORT_REST_PATH, createJsonRequest() );
        System.out.println( result );
    }

    private ObjectNode createJsonRequest()
    {
        final ObjectNode xslParamsJson = JsonHelper.newObjectNode();
        if ( xslParam != null )
        {
            for ( final String nameValue : xslParam )
            {
                if ( !nameValue.contains( "=" ) )
                {
                    throw new IllegalArgumentException(
                        "Invalid xsl parameter format, expected '=' as name-value separator: '" + nameValue + "'" );
                }
                final String[] parts = nameValue.split( "=", 2 );
                xslParamsJson.put( parts[0], parts[1] );
            }
        }
        final ObjectNode json = JsonHelper.newObjectNode();
        json.put( "exportName", this.exportName );
        json.put( "targetRepoPath", this.targetRepoPath );
        json.put( "importWithIds", !this.skipids );
        json.put( "importWithPermissions", !this.skipPermissions );
        json.put( "xslSource", this.xslSource );
        json.set( "xslParams", xslParamsJson );
        return json;
    }
}
