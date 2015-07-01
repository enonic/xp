package com.enonic.xp.toolbox;


import io.airlift.airline.Cli;
import io.airlift.airline.Help;
import io.airlift.airline.ParseException;

import com.enonic.xp.toolbox.repo.DeleteSnapshotsCommand;
import com.enonic.xp.toolbox.repo.DumpCommand;
import com.enonic.xp.toolbox.repo.ExportCommand;
import com.enonic.xp.toolbox.repo.ImportCommand;
import com.enonic.xp.toolbox.repo.ListSnapshotsCommand;
import com.enonic.xp.toolbox.repo.ReindexCommand;
import com.enonic.xp.toolbox.repo.RestoreCommand;
import com.enonic.xp.toolbox.repo.SnapshotCommand;

public final class Main
{
    public static void main( final String... args )
    {
        final Cli.CliBuilder<Runnable> builder = Cli.builder( "toolbox" );
        builder.withDescription( "Enonic XP CLI ToolBox" );
        builder.withDefaultCommand( Help.class );
        builder.withCommand( Help.class );
        builder.withCommand( DumpCommand.class );
        builder.withCommand( ExportCommand.class );
        builder.withCommand( ImportCommand.class );
        builder.withCommand( ReindexCommand.class );
        builder.withCommand( SnapshotCommand.class );
        builder.withCommand( RestoreCommand.class );
        builder.withCommand( DeleteSnapshotsCommand.class );
        builder.withCommand( ListSnapshotsCommand.class );
        builder.withCommand( DumpCommand.class );

        final Cli<Runnable> parser = builder.build();
        try
        {
            parser.parse( args ).run();
        }
        catch ( ParseException e )
        {
            System.err.println( e.getMessage() );
        }
        catch ( ResponseException e )
        {
            if ( e.getResponseCode() == 403 )
            {
                System.err.println( "Authentication failed \r\n" + e.getMessage() );
            }
            else
            {
                System.err.println( "Response error: " + e.getMessage() );
            }
        }
        catch ( RuntimeException e )
        {
            if ( e.getCause() instanceof java.net.ConnectException )
            {
                System.err.println( "Unable to connect to XP server: " + e.getMessage() );
            }
            else
            {
                System.err.println( "Unexpected error: " + e.getMessage() );
            }
        }
    }
}
