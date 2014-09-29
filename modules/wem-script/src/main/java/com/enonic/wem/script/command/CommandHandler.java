package com.enonic.wem.script.command;

public interface CommandHandler<C extends Command>
{
    public Class<C> getType();

    public C newCommand();

    public void invoke( C command );
}
