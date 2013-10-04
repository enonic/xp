package com.enonic.wem.core.command;

import com.enonic.wem.api.command.Command;

public abstract class CommandHandler<C extends Command>
{
    public abstract void handle( CommandContext context, C command )
        throws Exception;
}
