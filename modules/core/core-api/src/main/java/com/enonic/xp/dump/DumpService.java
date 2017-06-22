package com.enonic.xp.dump;

public interface DumpService
{
    SystemDumpResult dumpSystem( final SystemDumpParams params );

    SystemLoadResult loadSystemDump( final SystemLoadParams param );
}
