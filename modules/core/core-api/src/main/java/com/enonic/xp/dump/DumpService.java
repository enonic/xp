package com.enonic.xp.dump;

public interface DumpService
{
    void dump( final DumpParams params );

    void load( final LoadParams param );
}
