package com.enonic.xp.dump;

public interface DumpService
{
    SystemDumpResult systemDump( final SystemDumpParams params );

    SystemLoadResult loadSystemDump( final SystemLoadParams param );
}
