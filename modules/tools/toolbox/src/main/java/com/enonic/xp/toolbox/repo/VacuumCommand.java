package com.enonic.xp.toolbox.repo;

import io.airlift.airline.Command;

@Command(name = "vacuum", description = "Removes unused blobs and binaries from blobstore")
public class VacuumCommand
    extends RepoCommand
{

    private static final String VACUUM_REST_PATH = "/api/system/vacuum";

    @Override
    protected void execute()
        throws Exception
    {
        final String result = postRequest( VACUUM_REST_PATH, "{}" );
        System.out.println( result );
    }

}
