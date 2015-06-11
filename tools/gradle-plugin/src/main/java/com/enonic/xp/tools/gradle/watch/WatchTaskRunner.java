package com.enonic.xp.tools.gradle.watch;

import java.io.Closeable;
import java.io.PrintStream;

import org.gradle.api.Project;
import org.gradle.api.logging.LogLevel;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

import static org.gradle.api.logging.LogLevel.INFO;

final class WatchTaskRunner
    implements Closeable
{
    private final ProjectConnection connection;

    private final PrintStream out;

    public WatchTaskRunner( final Project project )
    {
        this.connection = GradleConnector.newConnector().
            useInstallation( project.getGradle().getGradleHomeDir() ).
            forProjectDirectory( project.getProjectDir() ).
            connect();

        this.out = System.out;

        final LogLevel logLevel = project.getGradle().getStartParameter().getLogLevel();
        if ( INFO.compareTo( logLevel ) < 0 )
        {
            System.setOut( new NopPrintStream() );
        }
    }

    public void run( final String task, final boolean initial )
    {
        if ( task == null )
        {
            return;
        }

        final BuildLauncher launcher = this.connection.newBuild();
        launcher.forTasks( task );

        if ( initial )
        {
            launcher.run( new InitStatusLogger( this.out ) );
        }
        else
        {
            launcher.run();
        }
    }

    @Override
    public void close()
    {
        this.connection.close();
    }
}
