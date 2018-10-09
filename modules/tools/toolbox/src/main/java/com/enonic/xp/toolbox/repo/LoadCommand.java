package com.enonic.xp.toolbox.repo;

import java.util.Scanner;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.airlift.airline.Command;
import io.airlift.airline.Option;

import com.enonic.xp.toolbox.util.JsonHelper;

@Command(name = "load", description = "Import data from a dump.")
public final class LoadCommand
    extends RepoCommand
{
    private static final String SYSTEM_DUMP_REST_PATH = "/api/system/load";

    @SuppressWarnings("WeakerAccess")
    @Option(name = "-s", description = "Dump name.", required = true)
    public String source;

    @SuppressWarnings("WeakerAccess")
    @Option(name = "-y", description = "Automatic yes to prompts; assume “Yes” as answer to all prompts and run non-interactively.")
    public boolean interactive = false;

    @SuppressWarnings("WeakerAccess")
    @Option(name = "--upgrade", description = "Upgrade the dump if necessary (default is false)")
    public boolean upgrade = false;


    @Override
    protected void execute()
        throws Exception
    {
        if ( !interactive )
        {
            System.out.println( "WARNING: This will delete all existing repositories that also present in the system-dump." );
            System.out.println( "Continue [Y/n]..." );
            Scanner scanner = new Scanner( System.in );
            String verify = scanner.next();

            if ( verify.equals( "Y" ) )
            {
                doLoad();
            }
        }
        else
        {
            doLoad();
        }
    }

    private void doLoad()
        throws Exception
    {
        System.out.println( "Loading dump..." );
        final String result = postRequest( SYSTEM_DUMP_REST_PATH, createJsonRequest() );
        System.out.println( "Dump loaded successfully" );
        System.out.println( result );
    }

    private ObjectNode createJsonRequest()
    {
        final ObjectNode json = JsonHelper.newObjectNode();
        json.put( "name", this.source );
        json.put( "upgrade", this.upgrade );
        return json;
    }
}
