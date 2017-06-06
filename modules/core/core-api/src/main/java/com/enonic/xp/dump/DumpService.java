package com.enonic.xp.dump;

public interface DumpService
{
    DumpResult dump( final DumpParams params );

    void load( final LoadParams param );
}
