package com.enonic.xp.toolbox;

import io.airlift.airline.Cli;
import io.airlift.airline.Help;

import com.enonic.xp.toolbox.repo.DeleteSnapshotsCommand;
import com.enonic.xp.toolbox.repo.ExportCommand;
import com.enonic.xp.toolbox.repo.ImportCommand;
import com.enonic.xp.toolbox.repo.ListSnapshotsCommand;
import com.enonic.xp.toolbox.repo.ReindexCommand;
import com.enonic.xp.toolbox.repo.RestoreCommand;
import com.enonic.xp.toolbox.repo.SnapshotCommand;
import com.enonic.xp.toolbox.upgrade.UpgradeCommand;

public final class Main
{
    public static void main( final String... args )
    {
        final Cli.CliBuilder<Runnable> builder = Cli.<Runnable>builder( "toolbox" );
        builder.withDescription( "Enonic XP CLI ToolBox" );
        builder.withDefaultCommand( Help.class );
        builder.withCommand( Help.class );
        builder.withCommand( UpgradeCommand.class );
        builder.withCommand( ExportCommand.class );
        builder.withCommand( ImportCommand.class );
        builder.withCommand( ReindexCommand.class );
        builder.withCommand( SnapshotCommand.class );
        builder.withCommand( RestoreCommand.class );
        builder.withCommand( DeleteSnapshotsCommand.class );
        builder.withCommand( ListSnapshotsCommand.class );

        final Cli<Runnable> parser = builder.build();
        parser.parse( args ).run();
    }
}
