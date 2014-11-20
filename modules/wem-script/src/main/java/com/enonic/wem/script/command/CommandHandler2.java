package com.enonic.wem.script.command;

public interface CommandHandler2
{
    public String getName();

    public Object execute( CommandRequest req );
}
