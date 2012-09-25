package com.enonic.wem.core.command;

import org.junit.Before;

public abstract class AbstractCommandHandlerTest
{
    protected CommandContext context;

    @Before
    public void setup()
    {
        // TODO: Will wire in JCR in-memory here
        this.context = new CommandContext();
    }
}
