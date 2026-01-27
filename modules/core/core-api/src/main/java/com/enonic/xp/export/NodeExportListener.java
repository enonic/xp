package com.enonic.xp.export;

public interface NodeExportListener
{
    void nodeExported( int count );

    void nodeResolved( int count );
}
