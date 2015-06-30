package com.enonic.xp.toolbox.repo;

import io.airlift.airline.Option;

import com.enonic.xp.toolbox.ToolCommand;

public abstract class RepoCommand
    extends ToolCommand
{
    @Option(name = "-a", description = "Authentication token for basic authentication (user:password).", required = true)
    public String auth;

    @Option(name = "-h", description = "Host name for server (default is localhost).")
    public String host = "localhost";

    @Option(name = "-p", description = "Port number for server (default is 8080).")
    public int port = 8080;
}
