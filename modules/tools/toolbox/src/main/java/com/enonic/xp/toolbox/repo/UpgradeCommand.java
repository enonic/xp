package com.enonic.xp.toolbox.repo;

import java.util.Scanner;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.airlift.airline.Command;
import io.airlift.airline.Option;

import com.enonic.xp.toolbox.util.JsonHelper;

@Command(name = "upgrade", description = "Upgrade a dump.")
public final class UpgradeCommand
    extends RepoCommand
{
    private static final String SYSTEM_UPGRADE_REST_PATH = "/api/system/upgrade";

    @SuppressWarnings("WeakerAccess")
    @Option(name = "-d", description = "Dump name.", required = true)
    public String name;


    @Override
    protected void execute()
        throws Exception
    {
        final String result = postRequest( SYSTEM_UPGRADE_REST_PATH, createJsonRequest() );
        System.out.println( result );
    }

    private ObjectNode createJsonRequest()
    {
        final ObjectNode json = JsonHelper.newObjectNode();
        json.put( "name", this.name );
        return json;
    }
}
