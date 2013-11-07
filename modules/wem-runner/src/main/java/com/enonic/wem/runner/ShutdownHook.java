package com.enonic.wem.runner;

final class ShutdownHook
    extends Thread
{
    private final Runner runner;

    public ShutdownHook( final Runner runner )
    {
        this.runner = runner;
    }

    @Override
    public void run()
    {
        this.runner.stop();
    }
}
