package com.enonic.wem.script.command;

public interface CommandHandler
{
    public String getName();

    public Object execute( CommandRequest req );
}
