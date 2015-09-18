package com.enonic.xp.toolbox.upgrade;

import java.io.File;

import io.airlift.airline.Command;
import io.airlift.airline.Option;

import com.enonic.xp.toolbox.ToolCommand;
import com.enonic.xp.upgrade.UpgradeException;
import com.enonic.xp.upgrade.UpgradeHandler;

@Command(name = "upgrade", description = "Upgrade a dump to the current version. The upgraded files will be written to <dumpFolderName>_upgraded_<version>")
public final class UpgradeCommand
    extends ToolCommand
{
    @Option(name = "-d", description = "Dump folder location", required = true)
    public String dump;

    @Override
    protected void execute()
        throws Exception
    {
        final File dumpDir = new File( this.dump );
        if ( !dumpDir.isDirectory() )
        {
            throw new UpgradeException( "Dump directory is not valid: " + dumpDir.getAbsolutePath() );
        }

        UpgradeHandler.create().
            sourceRoot( dumpDir.toPath() ).
            build().
            execute();
    }
}
