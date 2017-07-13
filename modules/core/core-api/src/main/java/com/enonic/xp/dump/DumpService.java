package com.enonic.xp.dump;

public interface DumpService
{
    SystemDumpResult dump( final SystemDumpParams params );

    SystemLoadResult load( final SystemLoadParams param );
}
