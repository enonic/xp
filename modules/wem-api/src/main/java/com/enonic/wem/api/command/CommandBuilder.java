package com.enonic.wem.api.command;

public abstract class CommandBuilder<C extends Command>
{
    protected final C command;

    public CommandBuilder( final C command )
    {
        this.command = command;
    }

    public final C build()
    {
        return this.command;
    }
}
