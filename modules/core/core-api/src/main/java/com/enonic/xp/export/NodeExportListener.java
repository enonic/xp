package com.enonic.xp.export;

public interface NodeExportListener
{
    void nodeExported( long count );

    void nodeResolved( long count );
}
