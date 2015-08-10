package com.enonic.xp.upgrade;

import java.nio.file.Path;
import java.nio.file.Paths;

public class UpgradePathHelper
{

    public static Path generateUpgradeTargetPath( final Path rootFolder, final String dumpName )
    {
        return Paths.get( rootFolder.toString(), dumpName + "_upgraded_" + UpgradeHandler.XP_VERSION );
    }

}
