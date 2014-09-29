package com.enonic.wem.script.command;

public interface CommandInvoker
    extends CommandFactory
{
    public void invokeCommand( Command command );
}
