package com.enonic.xp.tools.gradle.watch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.logging.StyledTextOutput;
import org.gradle.logging.StyledTextOutputFactory;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public class WatchTask
    extends DefaultTask
{
    private final Path projectPath;

    private File dir;

    private String task;

    private Watcher watcher;

    private WatchTaskRunner runner;

    public WatchTask()
    {
        this.projectPath = getProject().getProjectDir().toPath();
    }

    public void setDir( final File dir )
    {
        this.dir = dir;
    }

    public void setTask( final String task )
    {
        this.task = task;
    }

    @TaskAction
    public void watch()
        throws IOException
    {
        printDeprecationWarning();

        if ( this.task == null || this.dir == null )
        {
            return;
        }

        this.runner = new WatchTaskRunner( getProject() );
        this.runner.run( this.task, true );

        this.watcher = new WatcherImpl();
        this.watcher.register( this.dir.toPath() );

        try
        {
            while ( this.watcher.isOpen() )
            {
                watchLoop();
            }
        }
        finally
        {
            this.watcher.close();
            this.runner.close();
        }
    }

    private void watchLoop()
        throws IOException
    {
        WatchKey key;

        try
        {
            key = this.watcher.take();
        }
        catch ( final InterruptedException e )
        {
            return;
        }

        final boolean trigger = shouldTrigger( key );
        key.reset();

        if ( !trigger )
        {
            return;
        }

        this.runner.run( this.task, false );
    }

    private boolean shouldTrigger( final WatchKey key )
        throws IOException
    {
        final Path dir = (Path) key.watchable();
        for ( final WatchEvent<?> event : key.pollEvents() )
        {
            if ( event.kind() == OVERFLOW )
            {
                continue;
            }

            final Path name = (Path) event.context();
            final Path path = dir.resolve( name );

            if ( Files.isDirectory( path ) )
            {
                if ( event.kind() == ENTRY_CREATE )
                {
                    log( "Directory '{}' was created", this.projectPath.relativize( dir ) );
                    this.watcher.register( path );
                }
                continue;
            }

            if ( event.kind() == ENTRY_DELETE && this.watcher.isWatching( path ) )
            {
                log( "Directory '{}' was deleted", this.projectPath.relativize( dir ) );
                this.watcher.unregister( path );
                continue;
            }

            log( "File '{}' was {}", this.projectPath.relativize( path ), toString( event.kind() ) );
            return true;
        }

        return false;
    }

    private void log( final String message, final Object... args )
    {
        getLogger().lifecycle( message, args );
    }

    private String toString( final WatchEvent.Kind<?> eventKind )
    {
        if ( eventKind == ENTRY_CREATE )
        {
            return "created";
        }
        if ( eventKind == ENTRY_MODIFY )
        {
            return "changed";
        }
        if ( eventKind == ENTRY_DELETE )
        {
            return "deleted";
        }

        throw new IllegalStateException( String.valueOf( eventKind ) );
    }

    private void printDeprecationWarning()
    {
        final StyledTextOutput out = getServices().get( StyledTextOutputFactory.class ).create( WatchTask.class );

        out.println();
        out.style( StyledTextOutput.Style.Failure );
        out.println( "!! WARNING !!" );

        out.println();
        out.style( StyledTextOutput.Style.Info );
        out.println( "Watch task has been deprecated and will be removed in the next version. Please start using Gradle" );
        out.println( "continuous mode:" );
        out.println();
        out.println( "  gradle -t deploy" );
        out.println();
        out.println( "If you are running an old version of Gradle that does not support this, please upgrade Gradle." );
        out.println();
        out.println( "  * Continuous build: https://docs.gradle.org/current/userguide/continuous_build.html" );
        out.println( "  * Upgrade wrapper:  https://docs.gradle.org/current/userguide/gradle_wrapper.html" );
        out.println();
    }
}
