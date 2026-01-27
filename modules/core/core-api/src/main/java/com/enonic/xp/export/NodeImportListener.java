package com.enonic.xp.export;

public interface NodeImportListener
{
    void nodeImported( int count );

    void nodeResolved( int count );

    void nodeSkipped( int count );
}
