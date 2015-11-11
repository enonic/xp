package com.enonic.xp.toolbox;


import io.airlift.airline.Cli;
import io.airlift.airline.Help;
import io.airlift.airline.ParseException;

import com.enonic.xp.toolbox.app.InitAppCommand;
import com.enonic.xp.toolbox.repo.DeleteSnapshotsCommand;
import com.enonic.xp.toolbox.repo.DumpCommand;
import com.enonic.xp.toolbox.repo.ExportCommand;
import com.enonic.xp.toolbox.repo.ImportCommand;
import com.enonic.xp.toolbox.repo.ListSnapshotsCommand;
import com.enonic.xp.toolbox.repo.LoadCommand;
import com.enonic.xp.toolbox.repo.ReindexCommand;
import com.enonic.xp.toolbox.repo.RestoreCommand;
import com.enonic.xp.toolbox.repo.SetReplicasCommand;
import com.enonic.xp.toolbox.repo.SnapshotCommand;
import com.enonic.xp.toolbox.upgrade.UpgradeCommand;

public final class Main
{
    public static void main( final String... args )
    {
        final Cli.CliBuilder<Runnable> builder = Cli.builder( "toolbox" );
        builder.withDescription( "Enonic XP ToolBox" );
        builder.withDefaultCommand( Help.class );
        builder.withCommand( Help.class );
        builder.withCommand( DumpCommand.class );
        builder.withCommand( LoadCommand.class );
        builder.withCommand( ExportCommand.class );
        builder.withCommand( ImportCommand.class );
        builder.withCommand( ReindexCommand.class );
        builder.withCommand( SnapshotCommand.class );
        builder.withCommand( RestoreCommand.class );
        builder.withCommand( DeleteSnapshotsCommand.class );
        builder.withCommand( ListSnapshotsCommand.class );
        builder.withCommand( SetReplicasCommand.class );
        builder.withCommand( UpgradeCommand.class );
        builder.withCommand( InitAppCommand.class );

        final Cli<Runnable> parser = builder.build();

        try
        {
            parser.parse( args ).run();
        }
        catch ( final ParseException e )
        {
            System.err.println( e.getMessage() );
        }
    }
}
