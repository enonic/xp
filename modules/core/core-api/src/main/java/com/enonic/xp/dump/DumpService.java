package com.enonic.xp.dump;

import java.util.List;

public interface DumpService
{
    SystemDumpResult dump( SystemDumpParams params );

    SystemLoadResult load( SystemLoadParams param );

    DumpUpgradeResult upgrade( SystemDumpUpgradeParams params );

    List<SystemDumpEntry> list();
}
