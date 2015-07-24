package com.enonic.xp.toolbox.app;


import io.airlift.airline.Command;
import io.airlift.airline.Option;

import com.enonic.xp.toolbox.ToolCommand;

@Command(name = "init-app", description = "Initiates an Enonic XP application.")
public final class InitAppCommand
    extends ToolCommand
{

    @Option(name = {"-n", "--name"}, description = "Application name.", required = true)
    public String name;

    @Option(name = {"-v", "--version"}, description = "Version number.")
    public String version;

    @Option(name = {"-destination", "--destination"}, description = "Project path.")
    public String destination;

    @Override
    protected void execute()
        throws Exception
    {

    }
}
