package com.enonic.xp.toolbox.repo;

import io.airlift.airline.Command;
import io.airlift.airline.Option;

@Command(name = "dump", description = "Data dump tool")
public final class DumpCommand
    extends RepoCommand
{

    public static final String SYSTEM_DUMP_REST_PATH = "/admin/rest/system/dump";

    @Option(name = "-t", description = "Target path.", required = true)
    public String target;

    @Override
    protected void execute()
        throws Exception
    {
        final DumpJsonRequest request = new DumpJsonRequest().targetDirectory( target );
        final String jsonRequest = new RequestJsonSerializer().serialize( request );
        final String result = postRequest( SYSTEM_DUMP_REST_PATH, jsonRequest );

        System.out.println( result );
    }
}
