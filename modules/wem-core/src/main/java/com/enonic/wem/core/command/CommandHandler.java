package com.enonic.wem.core.command;

import com.enonic.wem.api.command.Command;

public abstract class CommandHandler<C extends Command>
{
    protected CommandContext context;

    protected C command;

    public final void setContext( final CommandContext context )
    {
        this.context = context;
    }

    public final void setCommand( final C command )
    {
        this.command = command;
    }

    public abstract void handle()
        throws Exception;
}
