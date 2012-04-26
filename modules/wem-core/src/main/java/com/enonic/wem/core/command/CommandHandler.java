package com.enonic.wem.core.command;

import com.enonic.wem.api.command.Command;

public abstract class CommandHandler<C extends Command>
{
    private final Class<C> type;

    public CommandHandler( final Class<C> type )
    {
        this.type = type;
    }

    public final Class<C> getType()
    {
        return this.type;
    }

    public abstract void handle( C command );
}
