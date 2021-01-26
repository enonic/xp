package com.enonic.xp.dump;

public interface DumpService
{
    SystemDumpResult dump( SystemDumpParams params );

    SystemLoadResult load( SystemLoadParams param );

    DumpUpgradeResult upgrade( SystemDumpUpgradeParams params );
}
