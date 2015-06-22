package com.enonic.xp.tools.gradle.watch;

import java.io.PrintStream;

import org.gradle.tooling.GradleConnectionException;
import org.gradle.tooling.ResultHandler;

final class InitStatusLogger
    implements ResultHandler<Void>
{
    private final PrintStream out;

    public InitStatusLogger( final PrintStream out )
    {
        this.out = out;
        this.out.print( "Start watching for changes..." );
        this.out.flush();
    }

    @Override
    public void onComplete( final Void result )
    {
        System.setOut( this.out );
        this.out.println( " OK" );
    }

    @Override
    public void onFailure( final GradleConnectionException failure )
    {
    }
}
