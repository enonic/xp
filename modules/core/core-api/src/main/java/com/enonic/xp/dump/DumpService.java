package com.enonic.xp.dump;

public interface DumpService
{
    DumpResults dumpSystem( final DumpParams params );

    void loadSystemDump( final LoadParams param );
}
