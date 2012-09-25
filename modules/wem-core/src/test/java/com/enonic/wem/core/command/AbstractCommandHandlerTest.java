package com.enonic.wem.core.command;

public abstract class AbstractCommandHandlerTest
{
    protected CommandContext context;

    public void initialize()
        throws Exception
    {
        // TODO: Will wire in JCR in-memory here
        this.context = new CommandContext();
    }
}
