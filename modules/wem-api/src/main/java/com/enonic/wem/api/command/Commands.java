package com.enonic.wem.api.command;

import com.enonic.wem.api.command.schema.SchemaCommands;

public final class Commands
{
    private static final SchemaCommands SCHEMA_COMMANDS = new SchemaCommands();

    private Commands()
    {
    }

    public static SchemaCommands schema()
    {
        return SCHEMA_COMMANDS;
    }

}
