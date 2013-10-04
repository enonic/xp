package com.enonic.wem.core.command;

import com.enonic.wem.api.command.Command;

public abstract class CommandHandler<C extends Command>
{
    protected CommandContext context;

    public final void setContext( final CommandContext context )
    {
        this.context = context;
    }

    public abstract void handle( C command )
        throws Exception;
}
