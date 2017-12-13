package com.enonic.xp.export;

public interface NodeImportListener
{
    void nodeImported( long count );

    void nodeResolved( long count );
}
