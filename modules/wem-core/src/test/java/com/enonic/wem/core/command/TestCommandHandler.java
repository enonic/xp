package com.enonic.wem.core.command;

public final class TestCommandHandler
    extends CommandHandler<TestCommand>
{
    protected int executeCount;

    protected Exception errorOnHandle;

    public TestCommandHandler()
    {
        this.executeCount = 0;
        this.errorOnHandle = null;
    }

    @Override
    public void handle()
        throws Exception
    {
        this.executeCount++;

        if ( this.errorOnHandle != null )
        {
            throw this.errorOnHandle;
        }

        this.command.setResult( "ok" );
    }
}
