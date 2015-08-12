package com.enonic.xp.upgrade;

import java.nio.file.Path;
import java.nio.file.Paths;

public class UpgradePathHelper
{

    public static Path generateUpgradeTargetPath( final Path rootFolder, final String dumpName )
    {
        String rootFolderString = rootFolder == null ? "" : rootFolder.toString();
        return Paths.get( rootFolderString, dumpName + "_upgraded_" + UpgradeHandler.XP_VERSION );
    }

}
