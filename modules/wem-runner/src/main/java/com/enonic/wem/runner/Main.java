package com.enonic.wem.runner;

public final class Main
{
    public static void main( final String... args )
        throws Exception
    {
        final Runner runner = new Runner();
        Runtime.getRuntime().addShutdownHook( new ShutdownHook( runner ) );
        runner.start();
    }
}
