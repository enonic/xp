package com.enonic.xp.portal.script.command;

public interface CommandHandler
{
    public String getName();

    public Object execute( CommandRequest req );
}
